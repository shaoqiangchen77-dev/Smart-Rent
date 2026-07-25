# OSS 图片上传配置说明

## 已配置信息

- **Bucket**: `smart-rent`
- **Endpoint**: `oss-cn-beijing.aliyuncs.com`（华北 2 北京）
- **访问域名**: `https://smart-rent.oss-cn-beijing.aliyuncs.com`
- **AK/SK**: 通过环境变量注入，不在 `application.yml` 中硬编码

## 后端代码位置

- 上传服务：`src/main/java/com/smartrent/house/service/OssService.java`
- 上传接口：`src/main/java/com/smartrent/house/controller/FileController.java`
- 配置文件：`src/main/resources/application.yml`

## 环境变量设置方式（三选一）

### 方式 1：双击启动脚本（推荐，开发环境）

直接双击运行：

```text
house-service/run-local.bat
```

该脚本已写入 AK/SK 并启动 `mvn spring-boot:run`。

### 方式 2：IDEA 运行配置

在 IDEA 的 **Run/Debug Configurations -> HouseServiceApplication -> Environment variables** 中添加：

```text
OSS_ACCESS_KEY_ID=YOUR_ACCESS_KEY_ID;OSS_ACCESS_KEY_SECRET=YOUR_ACCESS_KEY_SECRET
```

### 方式 3：Windows 系统环境变量

PowerShell（当前会话有效）：

```powershell
$env:OSS_ACCESS_KEY_ID="YOUR_ACCESS_KEY_ID"
$env:OSS_ACCESS_KEY_SECRET="by6zBjm6K2dt59nDgn1afo9GR0G1wt"
```

CMD（当前会话有效）：

```cmd
set OSS_ACCESS_KEY_ID=YOUR_ACCESS_KEY_ID
set OSS_ACCESS_KEY_SECRET=YOUR_ACCESS_KEY_SECRET
```

## 上传接口

通过网关统一入口访问：

```text
POST http://localhost:8080/api/file/upload
Content-Type: multipart/form-data
```

表单字段：

- `file`：待上传的图片文件（必填）
- `dir`：目录前缀，默认 `house/images`，可按业务改为 `avatar`、`contract` 等

返回示例：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "url": "https://smart-rent.oss-cn-beijing.aliyuncs.com/house/images/2026/07/25/xxxx.jpg"
  }
}
```

## 重要提醒

1. **AK/SK 保密**：`run-local.bat` 已加入 `.gitignore`，请勿将其提交到 Git。
2. **Bucket 权限**：如果前端需要直接通过返回的 URL 查看图片，请把 Bucket `smart-rent` 的读写权限设置为 **公共读**（私有 Bucket 返回的 URL 会 403）。
3. **生产环境**：生产部署请使用阿里云 RAM 子账号、KMS/环境变量或配置中心管理密钥，不要硬编码到代码或配置文件中。
