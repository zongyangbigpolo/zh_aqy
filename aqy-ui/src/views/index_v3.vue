<template>
  <div class="app-container">
    <!-- 顶部区域 -->
    <div class="top-bar">
      <div class="left">
        <h3 class="title">边坡监测系统</h3>
      </div>
      <div class="right">
        <el-button-group>
          <el-button
            type="primary"
            :plain="!isAddingDevice"
            @click="toggleAddDevice"
            size="small">
            <i class="el-icon-plus"></i> 添加设备
          </el-button>
          <el-button
            type="danger"
            plain
            @click="cancelAdd"
            v-if="isAddingDevice"
            size="small">
            取消添加
          </el-button>
        </el-button-group>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="main-content">
      <!-- 左侧设备列表 -->
      <div class="left-panel">
        <div class="panel-header">
          <h4>设备列表</h4>
          <el-button type="text" @click="refreshDevices">
            <i class="el-icon-refresh"></i> 刷新
          </el-button>
        </div>
        <div class="device-list">
          <el-table
            :data="devices"
            style="width: 100%"
            height="calc(100% - 50px)"
            :header-cell-style="{ background: '#f5f7fa' }"
            @row-click="handleRowClick"
            :row-class-name="tableRowClassName">
            <el-table-column prop="name" label="设备名称">
              <template slot-scope="scope">
                <i :class="getDeviceIcon(scope.row.type)"></i>
                {{ scope.row.name }}
              </template>
            </el-table-column>
            <el-table-column prop="type" label="设备类型">
              <template slot-scope="scope">
                {{ getDeviceTypeName(scope.row.type) }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template slot-scope="scope">
                <span :style="{ color: getDeviceStatus(scope.row).color }">
                  {{ getDeviceStatus(scope.row).text }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template slot-scope="scope">
                <el-button
                  type="text"
                  size="small"
                  @click.stop="showDeviceData(scope.row)">
                  查看数据
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <!-- 右侧地图区域 -->
      <div class="right-panel">
        <div class="map-container">
          <div
            class="slope-map"
            ref="mapContainer"
            @click="handleMapClick"
            :class="{ 'adding-device': isAddingDevice }">
            <img
              :src="mapImage"
              @load="onMapImageLoad"
              ref="mapImage">

            <!-- 设备标记点 -->
            <div v-for="device in devices"
                 :key="device.id"
                 class="device-marker"
                 :class="{
                   'selected': selectedDevice === device,
                   [device.type]: true
                 }"
                 :style="getMarkerPosition(device)"
                 @click.stop="showDeviceData(device)"
                 v-draggable="!isAddingDevice"
                 @dragend="handleDragEnd($event, device)">
              <el-tooltip :content="device.name">
                <i :class="getDeviceIcon(device.type)"></i>
              </el-tooltip>
            </div>
          </div>

          <!-- 添加提示信息 -->
          <div class="map-tip" v-if="isAddingDevice">
            <el-alert
              title="点击地图位置添加设备"
              type="info"
              :closable="false"
              show-icon>
            </el-alert>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加设备弹窗 -->
    <el-dialog
      title="添加设备"
      :visible.sync="addDeviceDialogVisible"
      width="400px"
      @close="cancelAdd">
      <el-form ref="deviceForm" :model="newDevice" :rules="deviceRules" label-width="80px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="newDevice.name" placeholder="请输入设备名称"></el-input>
        </el-form-item>
        <el-form-item label="设备类型" prop="type">
          <el-select v-model="newDevice.type" placeholder="请选择设备类型" style="width: 100%">
            <el-option
              v-for="item in deviceTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备编号" prop="serialNo">
          <el-input v-model="newDevice.serialNo" placeholder="请输入设备编号"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="cancelAdd">取 消</el-button>
        <el-button type="primary" @click="confirmAddDevice">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 设备数据弹窗 -->
    <el-dialog
      :visible.sync="dialogVisible"
      :title="currentDevice ? currentDevice.name : ''"
      width="500px">
      <div class="device-data">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="设备编号">
            {{ deviceData.serialNo }}
          </el-descriptions-item>
          <el-descriptions-item label="设备类型">
            {{ getDeviceTypeName(currentDevice ? currentDevice.type : '') }}
          </el-descriptions-item>
          <el-descriptions-item label="安装位置">
            X: {{ currentDevice ? currentDevice.x.toFixed(2) : '' }}%,
            Y: {{ currentDevice ? currentDevice.y.toFixed(2) : '' }}%
          </el-descriptions-item>
          <el-descriptions-item label="设备状态">
            <span v-if="currentDevice" :style="{ color: getDeviceStatus(currentDevice).color }">
              {{ getDeviceStatus(currentDevice).text }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item :label="getDataLabel(currentDevice ? currentDevice.type : '')" :span="2">
            {{ deviceData.latestValue }} {{ deviceData.unit }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'SlopeMonitoring',
  directives: {
    draggable: {
      bind(el, binding) {
        if (binding.value === false) return;

        el.style.cursor = 'move';
        el.onmousedown = function(e) {
          e.stopPropagation();
          const disX = e.clientX - el.offsetLeft;
          const disY = e.clientY - el.offsetTop;
          const parentRect = el.parentElement.getBoundingClientRect();

          document.onmousemove = function(e) {
            let left = e.clientX - disX;
            let top = e.clientY - disY;

            // 限制在父元素范围内
            left = Math.max(0, Math.min(left, parentRect.width));
            top = Math.max(0, Math.min(top, parentRect.height));

            // 转换为百分比
            const percentX = (left / parentRect.width) * 100;
            const percentY = (top / parentRect.height) * 100;

            el.style.left = `${percentX}%`;
            el.style.top = `${percentY}%`;
          };

          document.onmouseup = function() {
            document.onmousemove = null;
            document.onmouseup = null;

            // 触发dragend事件
            el.dispatchEvent(new CustomEvent('dragend', {
              detail: {
                x: parseFloat(el.style.left),
                y: parseFloat(el.style.top)
              }
            }));
          };
        };
      }
    }
  },
  data() {
    return {
      mapImage: require('@/assets/images/dt.png'),
      mapLoaded: false,
      mapSize: {
        width: 0,
        height: 0
      },
      isAddingDevice: false,
      addDeviceDialogVisible: false,
      devices: [],
      dialogVisible: false,
      currentDevice: null,
      selectedDevice: null,
      newDevice: {
        name: '',
        type: '',
        serialNo: '',
        x: 0,
        y: 0
      },
      deviceTypes: [
        { label: '位移传感器', value: 'displacement' },
        { label: '倾角传感器', value: 'tilt' },
        { label: '裂缝传感器', value: 'crack' },
        { label: '雨量传感器', value: 'rainfall' }
      ],
      deviceData: {
        serialNo: '',
        latestValue: 0,
        unit: ''
      },
      deviceRules: {
        name: [
          { required: true, message: '请输入设备名称', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择设备类型', trigger: 'change' }
        ],
        serialNo: [
          { required: true, message: '请输入设备编号', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    onMapImageLoad() {
      this.mapLoaded = true;
      const imgElement = this.$refs.mapImage;
      if (imgElement) {
        this.mapSize = {
          width: imgElement.offsetWidth,
          height: imgElement.offsetHeight
        };
      }
      this.initializeDevices();
    },

    initializeDevices() {
      // 从后端加载设备数据
      // this.loadDevicesFromServer();
    },

    refreshDevices() {
      this.initializeDevices();
      this.$message.success('刷新成功');
    },

    toggleAddDevice() {
      this.isAddingDevice = !this.isAddingDevice;
      if (!this.isAddingDevice) {
        this.cancelAdd();
      }
    },

    handleMapClick(e) {
      if (!this.isAddingDevice) return;

      const rect = this.$refs.mapContainer.getBoundingClientRect();
      const x = ((e.clientX - rect.left) / rect.width) * 100;
      const y = ((e.clientY - rect.top) / rect.height) * 100;

      this.newDevice.x = x;
      this.newDevice.y = y;
      this.addDeviceDialogVisible = true;
    },

    confirmAddDevice() {
      this.$refs.deviceForm.validate((valid) => {
        if (valid) {
          const device = {
            id: Date.now(),
            ...this.newDevice
          };

          this.devices.push(device);
          this.addDeviceDialogVisible = false;
          this.isAddingDevice = false;
          this.newDevice = {
            name: '',
            type: '',
            serialNo: '',
            x: 0,
            y: 0
          };
          this.$message.success('添加设备成功');
        }
      });
    },

    cancelAdd() {
      this.isAddingDevice = false;
      this.addDeviceDialogVisible = false;
      this.newDevice = {
        name: '',
        type: '',
        serialNo: '',
        x: 0,
        y: 0
      };
    },

    handleDragEnd(e, device) {
      const { detail } = e;
      if (!detail) return;

      const { x, y } = detail;
      device.x = Math.max(0, Math.min(100, x));
      device.y = Math.max(0, Math.min(100, y));
    },

    getDeviceIcon(type) {
      const icons = {
        displacement: 'el-icon-aim',
        tilt: 'el-icon-coordinate',
        crack: 'el-icon-warning',
        rainfall: 'el-icon-umbrella'
      };
      return icons[type] || 'el-icon-location-information';
    },

    getDeviceTypeName(type) {
      const deviceType = this.deviceTypes.find(item => item.value === type);
      return deviceType ? deviceType.label : '';
    },

    getDeviceUnit(type) {
      const units = {
        displacement: 'mm',
        tilt: '°',
        crack: 'mm',
        rainfall: 'mm/h'
      };
      return units[type] || '';
    },

    getDataLabel(type) {
      const labels = {
        displacement: '位移数据',
        tilt: '倾角数据',
        crack: '裂缝宽度',
        rainfall: '降雨量'
      };
      return labels[type] || '监测数据';
    },

    getDeviceStatus(device) {
      if (!device || !device.type) return { color: '#909399', text: '未知' };

      // 模拟数据，实际应该从设备数据中获取
      const value = this.deviceData.latestValue;

      // 不同类型设备的阈值
      const thresholds = {
        displacement: { warning: 50, danger: 80 },
        tilt: { warning: 30, danger: 45 },
        crack: { warning: 10, danger: 20 },
        rainfall: { warning: 25, danger: 40 }
      };

      const threshold = thresholds[device.type];

      if (value >= threshold.danger) {
        return { color: '#F56C6C', text: '告警' };
      } else if (value >= threshold.warning) {
        return { color: '#E6A23C', text: '预警' };
      }
      return { color: '#67C23A', text: '正常' };
    },

    getMarkerPosition(device) {
      return {
        left: `${device.x}%`,
        top: `${device.y}%`
      }
    },

    showDeviceData(device) {
      this.currentDevice = device;
      this.selectedDevice = device;
      this.dialogVisible = true;
      this.fetchDeviceData(device.id);
    },

    handleRowClick(row) {
      this.selectedDevice = row;
      // 可以添加其他操作，比如在地图上定位到该设备
    },

    tableRowClassName({row}) {
      if (row === this.selectedDevice) {
        return 'selected-row';
      }
      return '';
    },

    async fetchDeviceData(deviceId) {
      try {
        // 这里添加获取设备数据的API调用
        // const response = await this.$api.getDeviceData(deviceId)
        // this.deviceData = response.data

        // 模拟数据
        this.deviceData = {
          serialNo: `DEV${deviceId}`,
          latestValue: Math.random() * 100,
          unit: 'mm'
        }
      } catch (error) {
        console.error('获取设备数据失败:', error);
        this.$message.error('获取设备数据失败');
      }
    }
  },
  mounted() {
    window.addEventListener('resize', this.onMapImageLoad);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.onMapImageLoad);
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
  height: calc(100vh - 84px);
  background: #f0f2f5;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0 10px;
}

.title {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.main-content {
  display: flex;
  height: calc(100% - 60px);
  gap: 20px;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.left-panel {
  width: 400px;
  background: #fff;
  border-right: 1px solid #e6e6e6;
}

.panel-header {
  padding: 15px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-header h4 {
  margin: 0;
  color: #303133;
}

.device-list {
  height: calc(100% - 51px);
}

.right-panel {
  flex: 1;
  min-width: 0;
}

.map-container {
  position: relative;
  height: 100%;
  background: #fff;
  border-radius: 4px;
}

.slope-map {
  height: 100%;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  overflow: hidden;
}

.slope-map img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.slope-map.adding-device {
  cursor: crosshair;
}

.device-marker {
  position: absolute;
  transform: translate(-50%, -50%);
  cursor: pointer;
  color: #409EFF;
  font-size: 24px;
  transition: all 0.3s;
  z-index: 10;
}

.device-marker:hover {
  color: #66b1ff;
  transform: translate(-50%, -50%) scale(1.2);
}

.device-marker.selected {
  color: #67C23A;
}

.map-tip {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
}

.device-data {
  padding: 20px;
}

/* 表格样式 */
/deep/ .el-table {
  border: none;
}

/deep/ .el-table::before {
  display: none;
}

/deep/ .el-table th {
  background: #f5f7fa;
}

/deep/ .el-table td,
/deep/ .el-table th {
  padding: 8px 0;
}

/* 选中行样式 */
/deep/ .el-table .selected-row {
  background-color: #f0f9eb;
}

/* 设备图标样式 */
.device-list i {
  margin-right: 5px;
  font-size: 16px;
}

/* 添加动画效果 */
.device-marker {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.8);
  }
  to {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
}

.device-marker.rainfall {
  color: #409EFF;
}

.device-marker.rainfall:hover {
  color: #66b1ff;
}

.device-marker.rainfall.selected {
  color: #67C23A;
}
</style>
