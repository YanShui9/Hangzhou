# 修复园区类型数据 - PowerShell脚本
# 将数字类型(1/2)转换为字符串类型(生产性制造类/生产性服务类)

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "开始执行数据库更新脚本" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# MySQL连接信息
$mysqlHost = "localhost"
$mysqlPort = "3306"
$mysqlUser = "root"
$mysqlPassword = "123456"  # 请根据实际情况修改密码
$database = "park_evaluation"

# SQL语句
$sql1 = "UPDATE park_info SET park_type = '生产性制造类' WHERE park_type = '1';"
$sql2 = "UPDATE park_info SET park_type = '生产性服务类' WHERE park_type = '2';"
$sql3 = "SELECT id, park_name, park_type FROM park_info;"

Write-Host "正在连接MySQL数据库..." -ForegroundColor Yellow

# 尝试使用常见的MySQL路径
$mysqlPaths = @(
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe",
    "C:\xampp\mysql\bin\mysql.exe",
    "D:\xampp\mysql\bin\mysql.exe",
    "C:\wamp\bin\mysql\mysql8.0.27\bin\mysql.exe"
)

$mysqlExe = $null
foreach ($path in $mysqlPaths) {
    if (Test-Path $path) {
        $mysqlExe = $path
        Write-Host "找到MySQL: $path" -ForegroundColor Green
        break
    }
}

if (-not $mysqlExe) {
    Write-Host "未找到MySQL可执行文件!" -ForegroundColor Red
    Write-Host "`n请手动在MySQL客户端执行以下SQL:" -ForegroundColor Yellow
    Write-Host "USE park_evaluation;" -ForegroundColor White
    Write-Host $sql1 -ForegroundColor White
    Write-Host $sql2 -ForegroundColor White
    Write-Host "`n或者配置MySQL的PATH环境变量后重新运行此脚本`n" -ForegroundColor Yellow
    exit 1
}

# 执行更新
Write-Host "`n执行更新SQL..." -ForegroundColor Yellow
& $mysqlExe -h $mysqlHost -P $mysqlPort -u $mysqlUser -p$mysqlPassword $database -e $sql1
& $mysqlExe -h $mysqlHost -P $mysqlPort -u $mysqlUser -p$mysqlPassword $database -e $sql2

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ 更新成功!" -ForegroundColor Green
} else {
    Write-Host "✗ 更新失败,请检查数据库连接和密码" -ForegroundColor Red
    exit 1
}

# 验证结果
Write-Host "`n验证更新结果:" -ForegroundColor Yellow
& $mysqlExe -h $mysqlHost -P $mysqlPort -u $mysqlUser -p$mysqlPassword $database -e $sql3

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "数据库更新完成!" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan
