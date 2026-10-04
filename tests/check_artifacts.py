"""Validate release jars and identical world-generation data across loaders."""
import json, zipfile, hashlib, tomllib
from pathlib import Path
root=Path(__file__).resolve().parents[1]
properties=dict(line.strip().split('=',1) for line in (root/'gradle.properties').read_text().splitlines()
                if line.strip() and not line.lstrip().startswith('#'))
minecraft_version=properties['minecraft_version']
version=f"{properties['mod_version']}+{minecraft_version}"
reference=None
for loader in ('fabric','neoforge','forge'):
    jars=[p for p in (root/loader/'build/libs').glob('*.jar') if not p.name.endswith('-sources.jar')]
    assert len(jars)==1,(loader,jars)
    assert jars[0].name==f"{properties['mod_id']}-{loader}-{version}.jar",jars[0].name
    with zipfile.ZipFile(jars[0]) as z:
        names=z.namelist();assert len(names)==len(set(names)), 'Duplicate entries'
        assert 'LICENSE_larion' in names
        assert 'com/badgerson/larion/LarionMod.class' in names
        assert 'com/badgerson/larion/mixin/NoiseChunkGeneratorMixin.class' in names
        assert 'META-INF/services/com.badgerson.larion.platform.services.IPlatformHelper' in names
        resources={n:hashlib.sha256(z.read(n)).hexdigest() for n in names if n.startswith('data/') and n.endswith('.json')}
        assert resources
        if reference is None:reference=resources
        else:assert reference==resources,'Worldgen differs by loader'
        for n in names:
            if n.endswith('.json'):
                json.loads(z.read(n));assert b'${' not in z.read(n),(loader,n)
        pack=json.loads(z.read('pack.mcmeta'))['pack']
        assert pack['min_format']==[107,1] and pack['max_format']==[107,1]
        spaghetti=json.loads(z.read('data/minecraft/worldgen/density_function/overworld/caves/spaghetti_2d.json'))
        rarity=spaghetti['input']['argument1']['argument1']
        assert rarity['type']=='minecraft:abs'
        assert rarity['argument']['type']=='minecraft:interval_select'
        assert rarity['argument']['thresholds']==[-0.75,-0.5,0.5,0.75]
        dim=json.loads(z.read('data/minecraft/dimension_type/overworld.json'))
        assert (dim['min_y'],dim['height'],dim['logical_height'])==(-128,640,640)
        assert dim['attributes']['minecraft:visual/fog_color']=='#c0d8ff'
        if loader=='fabric':
            m=json.loads(z.read('fabric.mod.json'));assert m['depends']['minecraft']==properties['minecraft_version_range_fabric']
            assert m['version']==version
            assert z.read('larion.accesswidener').startswith(b'accessWidener v2 official')
        else:
            meta='META-INF/'+('mods.toml' if loader=='forge' else 'neoforge.mods.toml')
            m=tomllib.loads(z.read(meta).decode());assert m['mods'][0]['modId']=='larion'
            assert m['mods'][0]['version']==version
            dependency=next(d for d in m['dependencies']['larion'] if d['modId']=='minecraft')
            assert dependency['versionRange']==properties['minecraft_version_range']
            assert 'META-INF/accesstransformer.cfg' in names
            assert 'larion.accesswidener' not in names
            if loader=='forge':assert b'MixinConfigs: larion.mixins.json' in z.read('META-INF/MANIFEST.MF')
    print('PASS',loader,jars[0].name)
print('PASS: all loaders contain identical generation resources')
