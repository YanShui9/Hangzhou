import subprocess
import sys

mysql_path = r"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
sql_file = r"d:\zzjj12\Hangzhou\park-server\src\main\resources\sql\init_district_data.sql"

cmd = [mysql_path, "-u", "root", "-p123456", "--default-character-set=utf8mb4"]

with open(sql_file, "r", encoding="utf-8") as f:
    sql_content = f.read()

proc = subprocess.run(cmd, input=sql_content, capture_output=True, text=True, encoding="utf-8")

if proc.returncode == 0:
    print("SQL 执行成功!")
    if proc.stdout:
        print(proc.stdout)
else:
    print("SQL 执行失败:")
    print(proc.stderr)
    sys.exit(1)
