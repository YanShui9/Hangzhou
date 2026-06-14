$env:CLASSPATH = "D:\高级课设\23级实训\hangzhouxiaowei\Hangzhou\park-server\target\classes"
Get-ChildItem -Path "D:\maven" -Recurse -Filter "*.jar" | ForEach-Object {
    $env:CLASSPATH += ";$($_.FullName)"
}
Write-Host "Classpath set, starting application..."
java com.park.ParkServerApplication