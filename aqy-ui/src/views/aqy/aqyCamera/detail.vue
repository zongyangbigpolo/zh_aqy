<template>
  <div class="app-container">
    <el-dialog
      :title="titleProfile"
      :visible.sync="open"
      width="85%"
      :close-on-click-modal="false"
      class="showAll_dialog preview-dialog"
      append-to-body
      v-el-drag-dialog
      @close="handleClose"
    >
      <el-descriptions class="margin-top" title="设备信息" :column="3" :size="size" border direction="vertical">
        <el-descriptions-item>
          <template slot="label">设备名称</template>
          {{ this.detail.deviceName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">设备序列号</template>
          {{ this.detail.deviceSerial }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">设备型号</template>
          {{ this.detail.model }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">在线状态</template>
          <el-tag size="small" type="success" v-if="this.detail.onlineStatusStr === '在线'">{{ this.detail.onlineStatusStr }}</el-tag>
          <el-tag size="small" type="warning" v-else-if="this.detail.onlineStatusStr === '离线'">{{ this.detail.onlineStatusStr }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">固件版本</template>
          {{ this.detail.deviceVersion }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">设备模式</template>
          {{ this.detail.defenceStr }}
        </el-descriptions-item>
      </el-descriptions>

      <div class="preview-container">
        <div class="preview-header">
          <span class="camera-name">实时监控画面</span>
          <span class="countdown" v-if="remainingTime > 0">
            预览将在 {{ Math.ceil(remainingTime / 1000) }} 秒后自动关闭
          </span>
        </div>
        <div class="preview-content">
          <iframe
            :src="palyUrl"
            width="100%"
            height="600"
            id="ysOpenDevice"
            allowfullscreen
            frameborder="0"
          >
          </iframe>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  byDeviceSerial, getWebVideoUrl
} from "@/api/aqy/camera";

export default {
  name: "detail",
  data() {
    return {
      size: '100',
      titleProfile: null,
      palyUrl: null,
      open: false,
      detail: {},
      detailTimer: null,
      remainingTime: 120000,
      countdownTimer: null
    }
  },
  computed: {},
  created() {

  },
  methods: {
    init(hikParams, row) {
      hikParams.deviceSerial = row.qmtCode
      this.open = true;
      this.remainingTime = 120000;

      // 添加2分钟自动关闭
      if (this.detailTimer) {
        clearTimeout(this.detailTimer);
      }
      this.detailTimer = setTimeout(() => {
        this.handleClose();
        this.$message.info('详情页面超时，已自动关闭');
      }, 120000);

      // 启动倒计时
      this.startCountdown();

      if (row.type === 2) {
        this.palyUrl = `https://open.ys7.com/console/jssdk/pc.html?accessToken=${row.accessToken}&url=ezopen://open.ys7.com/${row.qmtCode}/1.hd.live`;
        this.detail = {
          deviceName: row.eqmtName,
          deviceSerial: row.qmtCode,
          model: row.model || '-',
          onlineStatusStr: row.onlineStatus === 1 ? '在线' : '离线',
          deviceVersion: row.deviceVersion || '-',
          defenceStr: row.defenceStr || '-'
        };
        this.titleProfile = `【${this.detail.deviceName}】详情`;
      } else {
        byDeviceSerial(hikParams).then(response => {
          this.detail = response.data.data[0];
          this.titleProfile = "【" + this.detail.deviceName + "】详情";

          getWebVideoUrl(hikParams).then(response => {
            this.palyUrl = response.data.data.previewUrl;
          });
        });
      }
    },

    startCountdown() {
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer);
      }
      this.countdownTimer = setInterval(() => {
        this.remainingTime -= 1000;
        if (this.remainingTime <= 0) {
          clearInterval(this.countdownTimer);
        }
      }, 1000);
    },

    handleClose() {
      this.open = false;
      this.palyUrl = '';
      if (this.detailTimer) {
        clearTimeout(this.detailTimer);
      }
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer);
      }
      this.remainingTime = 120000;
    },

    beforeDestroy() {
      if (this.detailTimer) {
        clearTimeout(this.detailTimer);
      }
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer);
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.preview-dialog {
  ::v-deep .el-dialog__header {
    background: #1a1a1a;
    padding: 15px 20px;
    .el-dialog__title {
      color: #fff;
      font-size: 18px;
      font-weight: 500;
    }
    .el-dialog__headerbtn .el-dialog__close {
      color: #fff;
      font-size: 20px;
    }
  }

  ::v-deep .el-dialog__body {
    padding: 20px;
    background: #fff;
  }

  ::v-deep .el-descriptions {
    margin-bottom: 20px;
  }
}

.preview-container {
  background: #000;
  border-radius: 4px;
  overflow: hidden;
  margin-top: 20px;

  .preview-header {
    padding: 15px 20px;
    background: #2c2c2c;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .camera-name {
      color: #fff;
      font-size: 16px;
      font-weight: 500;
    }

    .countdown {
      color: #ff9900;
      font-size: 14px;
    }
  }

  .preview-content {
    background: #000;
    display: flex;
    justify-content: center;
    align-items: center;

    iframe {
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    }
  }
}

::v-deep .el-descriptions__body {
  background-color: #f8f9fa;

  .el-descriptions-item__label {
    background-color: #f0f2f5;
    color: #606266;
    font-weight: 500;
  }

  .el-descriptions-item__content {
    color: #303133;
  }
}
</style>
