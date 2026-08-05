## 中瀚安全云平台前端

前端基于 Vue 2.7 + Vue CLI 5 + Webpack 5 + Element UI。

### 开发

```bash
npm install --legacy-peer-deps
npm run dev
```

浏览器访问 `http://localhost:80`。

### 构建

```bash
CI=false NODE_OPTIONS=--max-old-space-size=4096 npm run build:prod
```

生产构建结果位于 `dist/`，Release 包和 Docker 镜像会使用该目录。
