
<#
.SYNOPSIS
智慧公园管理平台后端服务重启脚本
自动处理端口占用、进程管理和服务验证

.DESCRIPTION
该脚本提供完整的后端服务重启流程：
1. 停止占用8080端口的旧进程
2. 等待端口释放
3. 使用java -jar启动后端服务
4. 验证服务是否正常启动
#>

$port = 8080
$jarPath = "d:\zzjj12\Hangzhou\park-server\target\park-server-1.0.0.jar"
$workingDir = "d:\zzjj12\Hangzhou\park-server"

Write-Host "`n==========================================" -ForegroundColor Cyan
Write-Host "智慧公园管理平台后端服务重启脚本" -ForegroundColor Cyan
Write-Host "==========================================`n" -ForegroundColor Cyan

# 1. 查找并停止占用8080端口的进程
Write-Host "[1/4] 正在查找占用端口 $port 的进程..." -ForegroundColor Yellow

$processInfo = netstat -ano | findstr ":$port"
if ($processInfo) {
    $targetPid = $processInfo.Split(' ')[-1].Trim()
    Write-Host "发现进程 PID: $targetPid 占用端口 $port" -ForegroundColor Red
    
    try {
        Stop-Process -Id $targetPid -Force -ErrorAction Stop
        Write-Host "已成功停止进程 $targetPid" -ForegroundColor Green
    }
    catch {
        Write-Host "停止进程失败: $_" -ForegroundColor Red
    }
    
    # 等待端口释放
    Write-Host "等待端口 $port 释放..." -ForegroundColor Yellow
    $waitCount = 0
    while ($waitCount -lt 10) {
        $checkPort = netstat -ano | findstr ":$port"
        if (-not $checkPort) {
            Write-Host "端口 $port 已释放" -ForegroundColor Green
            break
        }
        Start-Sleep -Seconds 1
        $waitCount++
    }
}
else {
    Write-Host "端口 $port 未被占用" -ForegroundColor Green
}

# 2. 编译项目（如果需要）
Write-Host "`n[2/4] 检查并编译项目..." -ForegroundColor Yellow
if (-not (Test-Path $jarPath)) {
    Write-Host "JAR文件不存在，开始编译..." -ForegroundColor Yellow
    Set-Location $workingDir
    mvn clean package -DskipTests -q
    if ($LASTEXITCODE -eq 0) {
        Write-Host "项目编译成功" -ForegroundColor Green
    }
    else {
        Write-Host "项目编译失败" -ForegroundColor Red
        exit 1
    }
}
else {
    Write-Host "JAR文件已存在，跳过编译" -ForegroundColor Green
}

# 3. 启动后端服务
Write-Host "`n[3/4] 启动后端服务..." -ForegroundColor Yellow
Set-Location $workingDir
Start-Process -FilePath "java" -ArgumentList "-Xms512m", "-Xmx1024m", "-jar", $jarPath -WorkingDirectory $workingDir

# 4. 验证服务启动
Write-Host "`n[4/4] 验证服务启动..." -ForegroundColor Yellow
$waitCount = 0
$maxWait = 30
$serviceStarted = $false

while ($waitCount -lt $maxWait) {
    Start-Sleep -Seconds 2
    $waitCount++
    
    # 检查端口是否监听
    $checkPort = netstat -ano | findstr ":$port.*LISTENING"
    if ($checkPort) {
        Write-Host "端口 $port 已监听" -ForegroundColor Green
        
        # 尝试请求API
        try {
            $response = Invoke-RestMethod -Uri "http://localhost:$port/api/audits?pageNum=1&pageSize=10" -Method Get -TimeoutSec 5 -ErrorAction Stop
            Write-Host "API请求成功" -ForegroundColor Green
            $serviceStarted = $true
            break
        }
        catch {
            # 401未认证是正常的，说明服务已启动
            if ($_.Exception.Message -match "401") {
                Write-Host "服务已启动（需要登录认证）" -ForegroundColor Green
                $serviceStarted = $true
                break
            }
            Write-Host "等待服务初始化... ($waitCount/$maxWait)" -ForegroundColor Gray
        }
    }
    else {
        Write-Host "等待端口监听... ($waitCount/$maxWait)" -ForegroundColor Gray
    }
}

if ($serviceStarted) {
    Write-Host "`n==========================================" -ForegroundColor Green
    Write-Host "后端服务重启成功！" -ForegroundColor Green
    Write-Host "服务地址: http://localhost:$port" -ForegroundColor Green
    Write-Host "接口文档: http://localhost:$port/swagger-ui.html" -ForegroundColor Green
    Write-Host "==========================================`n" -ForegroundColor Green
}
else {
    Write-Host "`n==========================================" -ForegroundColor Red
    Write-Host "后端服务启动超时！" -ForegroundColor Red
    Write-Host "请检查日志或手动启动" -ForegroundColor Red
    Write-Host "==========================================`n" -ForegroundColor Red
    exit 1
}
