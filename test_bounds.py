"""Compile the actual reviewed Java math methods without Minecraft dependencies."""
from pathlib import Path
import re, subprocess, tempfile, sys
r=Path(sys.argv[1] if len(sys.argv)>1 else '.').resolve()
b=r/'common/src/main/java/com/badgerson/larion/density_function_types'
def method(text,name):
    m=re.search(r'(?:public|private) double '+name+r'\([^)]*\)\s*\{',text)
    start=m.start(); end=m.end(); depth=1
    while depth:
        if text[end]=='{':depth+=1
        if text[end]=='}':depth-=1
        end+=1
    return text[start:end]
d=(b/'Division.java').read_text();s=(b/'Sqrt.java').read_text()
source='''public class BoundsTest {
 record Range(double minValue, double maxValue) {}
 static class Division {
 Range argument1, argument2;
 Division(Range a, Range b) {argument1=a; argument2=b;}
'''+ '\n'.join(method(d,n) for n in ('minValue','maxValue','bound'))+'''
 }
 static class Sqrt {
 Range df;
 Sqrt(Range r) {df=r;}
'''+ '\n'.join(method(s,n) for n in ('minValue','maxValue','transform'))+'''
 }
 static void check(boolean ok) {if (!ok) throw new AssertionError();}
 public static void main(String[] args) {
 Division d=new Division(new Range(2,4),new Range(1,2));
 check(d.minValue()==1 && d.maxValue()==4);
 d=new Division(new Range(2,4),new Range(-1,1));
 check(d.minValue()==Double.NEGATIVE_INFINITY && d.maxValue()==Double.POSITIVE_INFINITY);
 d=new Division(new Range(2,4),new Range(0,0));
 check(d.minValue()==0 && d.maxValue()==0);
 d=new Division(new Range(Double.NEGATIVE_INFINITY,4),new Range(1,2));
 check(d.minValue()==Double.NEGATIVE_INFINITY && d.maxValue()==Double.POSITIVE_INFINITY);
 Sqrt s=new Sqrt(new Range(-4,9)); check(s.minValue()==0 && s.maxValue()==3);
 s=new Sqrt(new Range(-4,-1)); check(s.minValue()==0 && s.maxValue()==0);
 s=new Sqrt(new Range(4,9)); check(s.minValue()==2 && s.maxValue()==3);
 java.util.Random random=new java.util.Random(42);
 for(int i=0;i<10000;i++) {
 double a=random.nextDouble()*200-100, c=a+random.nextDouble()*100;
 double b=random.nextDouble()*200-100, e=b+random.nextDouble()*100;
 d=new Division(new Range(a,c),new Range(b,e));
 for(int j=0;j<20;j++) {
 double x=a+(c-a)*random.nextDouble(), y=b+(e-b)*random.nextDouble();
 double q=y==0?0:x/y;
 check(q>=d.minValue() && q<=d.maxValue());
 }
 }
 System.out.println("PASS: regression cases and 200000 sampled quotients");
 }
}
'''
with tempfile.TemporaryDirectory(prefix='larion-bounds-') as tmp:
    p=Path(tmp)/'BoundsTest.java';p.write_text(source)
    subprocess.run(['javac',str(p)],check=True)
    subprocess.run(['java','-cp',tmp,'BoundsTest'],check=True)
