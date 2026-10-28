"""Portable, synthetic Recursos checks. No database, report upload or host writes.
Java 8 required. Diagnostic compatibility changes only a temporary resource copy.
"""
from pathlib import Path
import argparse, hashlib, json, os, subprocess, sys

HERE=Path(__file__).resolve().parent
REPO=HERE.parents[1]
parser=argparse.ArgumentParser()
parser.add_argument('--java-home',default=os.environ.get('JAVA_HOME'))
args=parser.parse_args()
if not args.java_home:
    parser.error('Set JAVA_HOME to Java 8 or use --java-home')
java=Path(args.java_home)/'bin'/('java.exe' if os.name=='nt' else 'java')
javac=java.with_name('javac.exe' if os.name=='nt' else 'javac')
version=subprocess.run([str(java),'-version'],capture_output=True,text=True)
if '"1.8.' not in version.stderr+version.stdout:
    parser.error('Actual Java 8/Nashorn is required')
build=REPO/'target/recursos-checks'
deps=build/'dependencies.txt'
if not deps.is_file():
    parser.error('Run Maven test-compile dependency:build-classpath with mdep.outputFile=target/recursos-checks/dependencies.txt first')
results=HERE/'resultados';results.mkdir(exist_ok=True)
classes=build/'classes';classes.mkdir(parents=True,exist_ok=True)
cp=os.pathsep.join([str(REPO/'target/classes'),str(REPO/'src/main/resources'),deps.read_text(encoding='utf-8-sig').strip()])
compiled=subprocess.run([str(javac),'-encoding','UTF-8','-source','8','-target','8','-cp',cp,'-d',str(classes),str(HERE/'java/validation/Resources5gChecks.java')],capture_output=True,text=True)
(results/'compile.log').write_text(compiled.stdout+compiled.stderr,encoding='utf-8')
if compiled.returncode:
    print('Compilation failed: '+str(results/'compile.log'));sys.exit(2)
source=(REPO/'src/main/resources/scripts/planner_5g.js').read_bytes()
compat=build/'compat/scripts';compat.mkdir(parents=True,exist_ok=True)
shim=b"// DIAGNOSTIC COPY ONLY: not a change to the published engine.\nif(typeof Float64Array!=='undefined'&&!Float64Array.prototype.fill){Object.defineProperty(Float64Array.prototype,'fill',{value:function(value){for(var i=0;i<this.length;i++)this[i]=value;return this;},writable:true,configurable:true});}\n"
(compat/'planner_5g.js').write_bytes(shim+source)
(results/'runtime-identification.json').write_text(json.dumps({'baselineEngineSha256':hashlib.sha256(source).hexdigest(),'compatEngineSha256':hashlib.sha256(shim+source).hexdigest(),'scope':'synthetic checks; no Oracle; compatibility copy is diagnostic only'},indent=2),encoding='utf-8')
overall=0
for mode in ['baseline','compatfill']:
    run_cp=os.pathsep.join([str(classes),cp])
    if mode=='compatfill':run_cp=str(compat.parent)+os.pathsep+run_cp
    with (results/f'{mode}.log').open('w',encoding='utf-8') as log:
        run=subprocess.run([str(java),'-Xmx1536m','-Dfile.encoding=UTF-8','-cp',run_cp,'validation.Resources5gChecks',str(HERE),mode],stdout=log,stderr=subprocess.STDOUT)
    out=results/f'java-service-checks-{mode}.json'
    if not out.is_file():
        print(mode+': harness failed; read '+str(results/f'{mode}.log'));sys.exit(2)
    summary=json.loads(out.read_text(encoding='utf-8'))
    print(json.dumps({'mode':mode,'pass':summary['pass'],'fail':summary['fail'],'blocked':summary['blocked'],'processExit':run.returncode}))
    overall=max(overall,run.returncode)
    if run.returncode>1:break
sys.exit(overall)
