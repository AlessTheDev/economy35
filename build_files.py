import os
import shutil
from pathlib import Path
import dotenv
import subprocess
import sys


# copy assets from the src folder to the build folder
curdir = Path(__file__).parent
root = Path("src/main/java/org/cup")
assets =  root / "assets"
exe_dir = Path("build/executable/")
main_dir = exe_dir / "main"

NOT_SET = "..."


def try_to_copy(src: str, dest: str):
    try:
        shutil.copytree(src, dest)
    except:
        print("no: " , src)
        pass

try_to_copy(assets / "UI" / "fonts", main_dir / "UI"/ "fonts")
try_to_copy(assets / "audio", main_dir / "audio")
try_to_copy(assets / "sprites", main_dir / "sprites")


try:
    shutil.copy(root / "logo.png", main_dir / "logo.png")
except:
    pass


# ==== JAVA STUFF =====

# copy the jre to the build dir

def find_jre_path() -> str | None:
    if (envpath := curdir / ".env").exists():
        dotenv.load_dotenv(envpath)
    
    if jre_home := os.getenv("JRE_HOME", default=None):
        if jre_home != NOT_SET:
            return jre_home
    
    # try to determine the path
    if os.name == "nt":
        jre_home = subprocess.run('powershell java -XshowSettings:properties -version 2>&1 | findstr "java.home"', text=True, capture_output=True)
    else: # <- from wsl
        jre_home = subprocess.check_output('java.exe -XshowSettings:properties -version 2>&1 | grep "java.home"', text=True)
    
    if jre_home is not None:
        return jre_home.stdout.split("=")[-1].strip()

JRE_HOME = find_jre_path()

if not JRE_HOME:
    print("[Err] unable to copy the jre")
    sys.exit(1)

try_to_copy(JRE_HOME, exe_dir / "jre")
