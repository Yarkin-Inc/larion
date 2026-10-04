package com.badgerson.larion;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import com.badgerson.larion.density_function_types.*;

public class LarionMod implements ModInitializer {

  @Override
  public void onInitialize() {

    // This method is invoked by the Fabric mod loader when it is ready
    // to load your mod. You can access Fabric and Common code in this
    // project.

    Registry.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE,
        Identifier.fromNamespaceAndPath(Constants.MOD_ID, "div"), Division.CODEC);
    Registry.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE,
        Identifier.fromNamespaceAndPath(Constants.MOD_ID, "sqrt"), Sqrt.CODEC);
    Registry.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE,
        Identifier.fromNamespaceAndPath(Constants.MOD_ID, "signum"), Signum.CODEC);
    Registry.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE,
        Identifier.fromNamespaceAndPath(Constants.MOD_ID, "sine"), Sine.CODEC);
    Registry.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE,
        Identifier.fromNamespaceAndPath(Constants.MOD_ID, "x"), XCoord.CODEC);
    Registry.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE,
        Identifier.fromNamespaceAndPath(Constants.MOD_ID, "z"), ZCoord.CODEC);
    Registry.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE,
        Identifier.fromNamespaceAndPath(Constants.MOD_ID, "flat_domain_warp"), FlatDomainWarp.CODEC);
    Registry.register(BuiltInRegistries.MATERIAL_CONDITION_TYPE,
        Identifier.fromNamespaceAndPath(Constants.MOD_ID, "somewhat_steep"),
        SomewhatSteepMaterialCondition.CODEC);

    // Use Fabric to bootstrap the Common mod.
    Constants.LOG.info("Larion World Generation: Registered custom entries");
    CommonClass.init();
  }
}
