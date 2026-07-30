<template>
  <div class="app-container">
    <el-dialog :title="titleProfile" :visible.sync="open" width="80%" :close-on-click-modal="false" class="showAll_dialog" append-to-body v-el-drag-dialog @close="handleClose">
      <el-descriptions class="margin-top" title="设备信息" :column="3" :size="size" border direction="vertical">
        <el-descriptions-item>
          <template slot="label">
            设备名称
          </template>
          {{ this.detail.deviceName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            设备序列号
          </template>
          {{ this.detail.deviceSerial }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            设备型号
          </template>
          {{ this.detail.model }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            在线状态
          </template>
          <el-tag size="small" type="success" v-if="this.detail.onlineStatusStr === '在线'">{{ this.detail.onlineStatusStr }}</el-tag>
          <el-tag size="small" type="warning" v-else-if="this.detail.onlineStatusStr === '离线'"> {{ this.detail.onlineStatusStr }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            固件版本
          </template>
          {{ this.detail.deviceVersion }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            设备模式
          </template>
          {{ this.detail.defenceStr }}
        </el-descriptions-item>
      </el-descriptions>
      <div class="content">
        <h3 style="margin-top: 20px; color:#303133; font-weight: bold;">监控预览</h3>
        <iframe
          :src="palyUrl"
          style="margin: 0 auto; display: block;"
          width="100%"
          height="600"
          id="ysOpenDevice"
          allowfullscreen
        >
        </iframe>
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
      detailTimer: null
    }
  },
  computed: {},
  created() {

  },
  methods: {
    init(hikParams, row) {
      console.log(row);
      hikParams.deviceSerial = row.qmtCode

      this.open = true;

      // 添加2分钟自动关闭
      if (this.detailTimer) {
        clearTimeout(this.detailTimer);
      }
      this.detailTimer = setTimeout(() => {
        this.handleClose();
        this.$message.info('详情页面超时，已自动关闭');
      }, 120000);

      if (row.type === 2) {
        // 萤石云接入方式
        this.palyUrl = `https://open.ys7.com/console/jssdk/pc.html?accessToken=${row.accessToken}&url=ezopen://open.ys7.com/${row.qmtCode}/1.hd.live`;

        this.detail = {
          deviceName: row.eqmtName,
          deviceSerial: row.qmtCode,
          model: row.model || '-',
          onlineStatusStr: row.onlineStatus === '1' ? '在线' : '离线',
          deviceVersion: row.deviceVersion || '-',
          defenceStr: row.defenceStr || '-'
        };
        this.titleProfile = `【${this.detail.deviceName}】详情`;
      } else {
        // 原有业务逻辑
        byDeviceSerial(hikParams).then(response => {
          this.detail = response.data.data[0];
          this.titleProfile = "【" + this.detail.deviceName + "】详情";

          getWebVideoUrl(hikParams).then(response => {
            this.palyUrl = response.data.data.previewUrl;
          });
        });
      }
    },
    // 添加处理关闭弹窗的方法
    handleClose() {
      this.open = false;
      // 清空 iframe 的 src，停止视频流
      this.palyUrl = '';
      if (this.detailTimer) {
        clearTimeout(this.detailTimer);
      }
    },
    // 组件销毁时清除定时器
    beforeDestroy() {
      if (this.detailTimer) {
        clearTimeout(this.detailTimer);
      }
    }
  }

}
</script>

<style scoped>

</style>
