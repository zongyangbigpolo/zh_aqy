<template>
  <div class="home-container">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="statistics-section">
      <el-col :span="6" v-for="(item, index) in deviceStats" :key="index">
        <div class="stat-card" :class="`card-${index + 1}`">
          <div class="card-content">
            <div class="card-icon">
              <i :class="item.icon"></i>
            </div>
            <div class="card-info">
              <h4>{{ item.name }}</h4>
              <div class="card-numbers">
                <span class="online">{{ item.online }}</span>
                <span class="divider">/</span>
                <span class="total">{{ item.total }}</span>
              </div>
              <div class="status-bar">
                <div class="progress" :style="{ width: `${statProgress(item)}%` }"></div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 中间区域：项目概况和天气 -->
    <el-row :gutter="20" class="middle-section">
      <el-col :span="16">
        <div class="project-card">
          <div class="card-header">
            <h3>项目概况</h3>
            <el-tag type="success" size="small">运行中</el-tag>
          </div>
          <div class="project-content">
            <h2 class="project-name">{{ projectInfo.name }}</h2>
            <p class="project-desc">{{ projectInfo.projectDesc }}</p>
            <div class="project-details">
              <div class="detail-item">
                <span class="label">业主单位</span>
                <span class="value">{{ projectInfo.yzCompany }}</span>
              </div>
              <div class="detail-item">
                <span class="label">施工单位</span>
                <span class="value">{{ projectInfo.jsCompany }}</span>
              </div>
              <div class="detail-item">
                <span class="label">项目地点</span>
                <span class="value">{{ projectInfo.city }}</span>
              </div>
              <div class="detail-item">
                <span class="label">开始时间</span>
                <span class="value">{{ projectInfo.projectStartDate }}</span>
              </div>

            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="weather-card">
          <div class="card-header">
            <h3>实时天气</h3>
            <el-button type="text" @click="getWeather">
              <i class="el-icon-refresh"></i>
              <span class="refresh-time">更新时间：{{ weatherInfo.updateTime }}</span>
            </el-button>
          </div>
          <div class="weather-content">
            <div class="weather-main">
              <div class="weather-icon">
                <i :class="getWeatherIcon(weatherInfo.type)"></i>
                <span class="weather-type">{{ weatherInfo.type }}</span>
              </div>
              <div class="temp-info">
                <span class="temperature">{{ weatherInfo.temperature }}</span>
                <span class="degree">°C</span>
              </div>
            </div>
            <div class="weather-details">
              <div class="detail-row">
                <div class="detail-item">
                  <i class="el-icon-wind-power"></i>
                  <div class="detail-text">
                    <span class="label">风向</span>
                    <span class="value">{{ weatherInfo.windDirection }}</span>
                  </div>
                </div>
                <div class="detail-item">
                  <i class="el-icon-wind-power"></i>
                  <div class="detail-text">
                    <span class="label">风力</span>
                    <span class="value">{{ weatherInfo.windPower }}</span>
                  </div>
                </div>
              </div>
              <div class="detail-row">
                <div class="detail-item">
                  <i class="el-icon-heavy-rain"></i>
                  <div class="detail-text">
                    <span class="label">湿度</span>
                    <span class="value">{{ weatherInfo.humidity }}%</span>
                  </div>
                </div>
                <div class="detail-item">
                  <i class="el-icon-sunrise"></i>
                  <div class="detail-text">
                    <span class="label">空气质量</span>
                    <span class="value">{{ weatherInfo.airQuality || '良好' }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 底部区域：快捷菜单和地图 -->
    <el-row :gutter="20" class="bottom-section">
      <el-col :span="12">
        <div class="quick-menu-card">
          <div class="card-header">
            <h3>快捷菜单</h3>
          </div>
          <div class="menu-grid">
            <div class="menu-item" v-for="(menu, index) in quickMenus" :key="index" @click="handleMenuClick(menu)">
              <i :class="menu.icon"></i>
              <span>{{ menu.name }}</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="map-card">
          <div class="card-header">
            <h3>项目位置</h3>
          </div>
          <div id="map-container" class="map-container"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import AMapLoader from '@amap/amap-jsapi-loader'
import axios from 'axios'
import {listAqyEquipment, listEqmtsGroupByType, selectAqyEquipmentListForReport} from "@/api/aqy/aqyEquipment";
import {getProject, listProject} from "@/api/aqy/project";
import { getAmapJsKey, getAmapWeatherKey } from '@/utils/externalConfig'
export default {
  name: 'HomePage',
  data() {
    return {
      // 遮罩层
      loading: false,
      projectOptions: [],
      projectId: null,
      eqmtByTypes: [],
      // 设备统计数据
      deviceStats: [
        {
          name: '位移监测',
          icon: 'el-icon-position',
          online: 12,
          total: 15
        },
        {
          name: '裂缝监测',
          icon: 'el-icon-warning',
          online: 8,
          total: 10
        },
        {
          name: '雨量监测',
          icon: 'el-icon-heavy-rain',
          online: 5,
          total: 5
        },
        {
          name: '倾角监测',
          icon: 'el-icon-refresh',
          online: 6,
          total: 8
        }
      ],
      // 项目信息
      projectInfo: {
        name: '',
        projectDesc: '',
        owner: '',
        location: '',
        startDate: ''
      },
      // 天气信息
      weatherInfo: {
        temperature: '--',
        type: '未知',
        windDirection: '--',
        windPower: '--',
        humidity: '--',
        updateTime: '--'
      },
      // 快捷菜单
      quickMenus: [
        {
          name: '实时数据',
          icon: 'el-icon-data-line',
          path: '/data/realtime',
          permission: ['data:realtime:list']
        },
        {
          name: '报警记录',
          icon: 'el-icon-warning',
          path: '/data/alarm',
          permission: ['data:alarm:list']
        },
        {
          name: '设备管理',
          icon: 'el-icon-setting',
          path: '/device/collect',
          permission: ['device:collect:list']
        },
        {
          name: '大屏展示',
          icon: 'el-icon-full-screen',
          path: '/screen'
        }
      ],
      // 天气预报数据
      weatherForecast: [
        { time: '现在', type: '晴', temp: 26 },
        { time: '11:00', type: '多云', temp: 28 },
        { time: '12:00', type: '多云', temp: 30 },
        { time: '13:00', type: '晴', temp: 31 },
        { time: '14:00', type: '晴', temp: 32 },
        { time: '15:00', type: '多云', temp: 31 },
        { time: '16:00', type: '阴', temp: 29 },
        { time: '17:00', type: '阴', temp: 27 }
      ],
      map: null,
      amap: null,
      weatherTimer: null,
      dataList: []
    }
  },
  mounted() {
    this.getProjects();
  },
  methods: {
    statProgress(item) {
      return item && item.total > 0 ? Math.round((item.online / item.total) * 100) : 0
    },
    getProjects() {
      listProject().then(response => {
        const rows = Array.isArray(response.rows) ? response.rows : []
        this.projectOptions = rows;
        if (!rows.length) {
          this.projectId = null;
          this.projectInfo = {};
          this.eqmtByTypes = [];
          this.dataList = [];
          this.deviceStats = this.createEmptyDeviceStats();
          this.destroyMap();
          return;
        }

        this.projectId = rows[0].id
        this.getProjectInfo(this.projectId);
        this.getEqmtGroupTypes(this.projectId);
        this.getEqmtList(this.projectId);
      });
    },
    createEmptyDeviceStats() {
      return [
        { name: '位移监测', icon: 'el-icon-position', online: 0, total: 0 },
        { name: '裂缝监测', icon: 'el-icon-warning', online: 0, total: 0 },
        { name: '雨量监测', icon: 'el-icon-heavy-rain', online: 0, total: 0 },
        { name: '倾角监测', icon: 'el-icon-refresh', online: 0, total: 0 }
      ]
    },
    getProjectInfo(projectId){
      if (!projectId) {
        this.projectInfo = {};
        return;
      }
      getProject(projectId).then(response => {
        this.projectInfo = response.data || {};
        this.initMap(this.projectInfo);
      });
    },
    getEqmtGroupTypes(projectId) {
      if (!projectId) {
        this.deviceStats = this.createEmptyDeviceStats();
        return;
      }
      listEqmtsGroupByType({projectId: projectId}).then(response => {
        // 处理返回的数据，转换成deviceStats需要的格式
        const items = Array.isArray(response.items) ? response.items : []
        this.deviceStats = items.map(item => {
          // 图标映射
          const iconMap = {
            '位移': 'el-icon-position',
            '裂缝': 'el-icon-warning',
            '雨量': 'el-icon-heavy-rain',
            '倾角': 'el-icon-refresh'
          }

          return {
            name: item.eqmtTypeName + '监测',
            icon: iconMap[item.eqmtTypeName] || 'el-icon-monitor',
            online: item.onlineCount || 0,
            total: item.totalCount || 0
          }
        })
        if (!this.deviceStats.length) {
          this.deviceStats = this.createEmptyDeviceStats();
        }
      });
    },
    getEqmtList(projectId){
      if (!projectId) {
        this.dataList = [];
        return;
      }
      selectAqyEquipmentListForReport({projectId: projectId}).then(response => {
        this.dataList = Array.isArray(response.items) ? response.items : [];
      });
    },
    // 初始化地图
    async initMap(projectInfo = {}) {
      if (!projectInfo || !projectInfo.id) {
        this.destroyMap();
        return;
      }
      const amapKey = getAmapJsKey()
      if (!amapKey) {
        this.destroyMap();
        return;
      }
      try {
        const AMap = await AMapLoader.load({
          key: amapKey,
          version: '2.0',
          plugins: ['AMap.ToolBar', 'AMap.Scale']
        })
        this.amap = AMap
        const center = this.getMapCenter(projectInfo)

        this.map = new AMap.Map('map-container', {
          zoom: 15,
          center,
          mapStyle: 'amap://styles/fresh'
        })

        // 显式设置地图属性
        this.map.setStatus({
          scrollWheel: true,
          zoomEnable: true,
          doubleClickZoom: true,
          touchZoom: true
        })

        // 设置缩放范围
        this.map.setZooms([3, 20])

        // 添加缩放控件
        this.map.addControl(new AMap.Scale())
        this.map.addControl(new AMap.ToolBar({
          position: 'RB'
        }))

        // 清除已有标记
        this.map.clearMap()

        // 创建标记点
        const marker = new AMap.Marker({
          position: center,
          anchor: 'center',
          offset: new AMap.Pixel(0, 0),
          zIndex: 100,
          content: this.createMarkerContent()
        })

        // 创建信息窗体
        const infoWindow = new AMap.InfoWindow({
          isCustom: true,
          content: this.createInfoWindowContent(),
          offset: new AMap.Pixel(0, -10),
          anchor: 'bottom-center'
        })

        // 使用闭包维护状态
        const state = {
          isOpen: false,
          timer: null,
          mouseInMarker: false,
          mouseInInfoWindow: false
        }

        // 添加事件监听
        marker.on('mouseover', () => {
          state.mouseInMarker = true
          if (!state.isOpen) {
            state.isOpen = true
            infoWindow.open(this.map, marker.getPosition())
          }
        })

        marker.on('mouseout', () => {
          state.mouseInMarker = false
          setTimeout(() => {
            if (!state.mouseInMarker && !state.mouseInInfoWindow) {
              state.isOpen = false
              infoWindow.close()
            }
          }, 200)
        })

        // 地图移动事件监听
        this.map.on('movestart', () => {
          // 移除了 setAnimation 调用
        })

        this.map.on('moveend', () => {
          // 确保标记点位置正确
          marker.setPosition(center)
        })

        marker.setMap(this.map)

      } catch (error) {
        console.error('地图加载失败:', error)
      }
    },
    getMapCenter(projectInfo) {
      const longitude = Number(projectInfo.longitude)
      const latitude = Number(projectInfo.latitude)
      if (Number.isFinite(longitude) && Number.isFinite(latitude)) {
        return [longitude, latitude]
      }
      return [118.600908, 31.650577]
    },
    destroyMap() {
      if (this.map && typeof this.map.destroy === 'function') {
        this.map.destroy()
      }
      this.map = null
      this.amap = null
    },

    // 创建标记点内容
    createMarkerContent() {
      const markerContent = document.createElement('div')
      markerContent.className = 'custom-marker'
      markerContent.style.cssText = `
        position: absolute;
        transform: translate(-50%, -50%);
        cursor: pointer;
      `

      const markerIcon = document.createElement('div')
      markerIcon.className = 'marker-icon'
      markerIcon.style.cssText = `
        width: 36px;
        height: 36px;
        background: linear-gradient(145deg, #3B82F6, #2563EB);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
        border: 2px solid rgba(255, 255, 255, 0.9);
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      `

      const icon = document.createElement('i')
      icon.className = 'el-icon-location'
      icon.style.cssText = `
        color: #fff;
        font-size: 18px;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      `

      markerIcon.appendChild(icon)
      markerContent.appendChild(markerIcon)

      return markerContent
    },

    // 创建信息窗体内容
    createInfoWindowContent() {
      const infoContent = document.createElement('div')
      infoContent.className = 'map-info-window'
      infoContent.innerHTML = `
        <div class="info-header">
          <div class="project-name">大黄山边坡项目</div>
          <div class="project-status">
            <i class="el-icon-success"></i>
            <span>正常运行中</span>
          </div>
        </div>
      `
      return infoContent
    },

    // 更新地图中心点和标记
    updateMapCenter(longitude, latitude) {
      if (this.map && this.amap) {
        const AMap = this.amap
        this.map.clearMap()
        this.map.setCenter([longitude, latitude])
        this.map.setZoom(14)

        // 创建标记内容
        const markerContent = document.createElement('div')
        markerContent.className = 'custom-marker'
        markerContent.innerHTML = `
          <div class="marker-icon">
            <i class="el-icon-location-information"></i>
          </div>
          <div class="marker-status active"></div>
        `

        // 创建标记
        const marker = new AMap.Marker({
          position: [longitude, latitude],
          content: markerContent,
          offset: new AMap.Pixel(-45, -90),
          anchor: 'bottom-center'
        })

        // 创建信息窗体内容
        const infoContent = document.createElement('div')
        infoContent.className = 'map-info-window'
        infoContent.innerHTML = `
          <div class="info-header">
            <div class="project-name">大黄山边坡项目</div>
            <div class="project-status">
              <i class="el-icon-success"></i>
              <span>正常运行中</span>
            </div>
          </div>
        `

        // 创建信息窗体
        const infoWindow = new AMap.InfoWindow({
          isCustom: true,
          content: infoContent,
          offset: new AMap.Pixel(0, -20),
          anchor: 'bottom-center',
          closeWhenClickMap: true
        })

        // 使用闭包保存状态
        const markerState = {
          timeoutId: null,
          isInfoWindowOpen: false
        }

        // 绑定鼠标移入事件
        marker.on('mouseover', () => {
          if (markerState.timeoutId) {
            clearTimeout(markerState.timeoutId)
          }
          infoWindow.open(this.map, marker.getPosition())
        })

        // 绑定鼠标移出事件
        marker.on('mouseout', () => {
          markerState.timeoutId = setTimeout(() => {
            infoWindow.close()
          }, 200)
        })

        // 为信息窗体添加鼠标事件监听
        infoWindow.on('open', () => {
          const infoWindowContent = document.querySelector('.map-info-window')
          if (infoWindowContent) {
            infoWindowContent.addEventListener('mouseover', () => {
              if (markerState.timeoutId) {
                clearTimeout(markerState.timeoutId)
              }
            })

            infoWindowContent.addEventListener('mouseout', () => {
              markerState.timeoutId = setTimeout(() => {
                infoWindow.close()
              }, 200)
            })
          }
        })

        marker.setMap(this.map)
      }
    },

    // 获取天气图标
    getWeatherIcon(type) {
      // 气类型映射到图标
      const iconMap = {
        '晴': 'el-icon-sunny',
        '多云': 'el-icon-cloudy',
        '阴': 'el-icon-partly-cloudy',
        '小雨': 'el-icon-light-rain',
        '中雨': 'el-icon-moderate-rain',
        '大雨': 'el-icon-heavy-rain',
        '雷阵雨': 'el-icon-storm-rain',
        '阵雨': 'el-icon-shower-rain',
        '雪': 'el-icon-snow',
        '雾': 'el-icon-fog',
        '霾': 'el-icon-haze'
      }

      // 处理天气类型中含特定关键的情况
      const weatherType = String(type || '').toLowerCase()
      if (weatherType.includes('雨')) return 'el-icon-heavy-rain'
      if (weatherType.includes('云')) return 'el-icon-cloudy'
      if (weatherType.includes('晴')) return 'el-icon-sunny'
      if (weatherType.includes('阴')) return 'el-icon-partly-cloudy'
      if (weatherType.includes('雪')) return 'el-icon-snow'
      if (weatherType.includes('雾')) return 'el-icon-fog'
      if (weatherType.includes('霾')) return 'el-icon-haze'

      // 返回映射的图标或默认图标
      return iconMap[type] || 'el-icon-sunny'
    },

    // 处理菜单点击
    handleMenuClick(menu) {
      // 检查权限
      if (menu.permission && !this.checkPermission(menu.permission)) {
        this.$modal.msgError('暂无权限访问');
        return;
      }

      // 处理大屏展示特殊情况
      if (menu.path === '/screen') {
        const routeData = this.$router.resolve({
          name: 'Screen',
          query: {
            projectId: this.projectId,
            timestamp: new Date().getTime()
          }
        });
        window.open(routeData.href, '_blank');
        return;
      }

      // 其他菜单使用 tab 方式打开
      const query = {
        projectId: this.projectId,
        timestamp: new Date().getTime()
      };

      // 使用 name 而不是 path 来打开页面
      switch (menu.path) {

        case '/data/realtime':
          this.$router.push({ name: 'Realtime', query });
          break;
        case '/data/alarm':
          this.$router.push({ name: 'Alarm', query });
          break;
        case '/device/collect':
          this.$router.push({ name: 'DeviceCollect', query });
          break;
        default:
          this.$router.push({ path: menu.path, query });
      }
    },

    // 检查权限方法
    checkPermission(permissions) {
      return this.$auth.hasPermi(permissions);
    },

    // 获取天气信息
    async getWeather() {
      const weatherKey = getAmapWeatherKey()
      if (!weatherKey) {
        return;
      }
      try {
        // 使用高德地图天气API
        const response = await axios.get('https://restapi.amap.com/v3/weather/weatherInfo', {
          params: {
            key: weatherKey,
            city: '340500', // 马鞍山市的行政区划代码
            extensions: 'base'
          }
        })

        if (response.data.status === '1' && response.data.lives?.length > 0) {
          const weatherData = response.data.lives[0]
          this.weatherInfo = {
            temperature: weatherData.temperature,
            type: weatherData.weather,
            windDirection: weatherData.winddirection,
            windPower: weatherData.windpower + '级',
            humidity: weatherData.humidity,
            updateTime: weatherData.reporttime.split(' ')[1]
          }
        }
      } catch (error) {
        console.error('获取天气信息失败:', error)
      }
    },

    // 定时刷新天气
    startWeatherRefresh() {
      // 立即获取一次天气
      this.getWeather()
      // 每30分钟更新一次天气信息
      this.weatherTimer = setInterval(() => {
        this.getWeather()
      }, 30 * 60 * 1000)
    },

    // 添加标记点击处理方法
    handleMarkerClick() {
      // 根据需要跳转到指定页面
      this.$router.push({
        path: '/screen', // 替换为您想要跳转的路由路径
        query: {
          projectId: this.projectId // 可以传递参数
        }
      });

      // 如果需要在新窗口打开，可以使用：
      // window.open('/screen', '_blank');
    }
  },
  created() {
    this.startWeatherRefresh()
  },
  beforeDestroy() {
    // 清理定时器
    if (this.weatherTimer) {
      clearInterval(this.weatherTimer)
    }
    this.destroyMap()
  }
}
</script>

<style lang="scss" scoped>
.home-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: 100vh;

  // 统一行间距
  .el-row {
    margin-bottom: 20px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  // 统计卡片样式
  .statistics-section {
    .stat-card {
      background: white;
      border-radius: 12px;
      padding: 24px;
      height: 140px; // 统一高度
      box-shadow: 0 2px 12px rgba(0,0,0,0.05);
      transition: all 0.3s;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
      }

      .card-content {
        height: 100%;
        display: flex;
        align-items: center;

        .card-icon {
          width: 56px;
          height: 56px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16px;
          background: #ecf5ff;

          i {
            font-size: 28px;
            color: #409EFF;
          }
        }

        .card-info {
          flex: 1;

          h4 {
            margin: 0;
            font-size: 15px;
            color: #606266;
          }

          .card-numbers {
            margin: 8px 0;

            .online {
              font-size: 28px;
              font-weight: bold;
              color: #409EFF;
            }

            .divider {
              margin: 0 4px;
              color: #909399;
            }

            .total {
              font-size: 20px;
              color: #606266;
            }
          }

          .status-bar {
            height: 4px;
            background: #f0f0f0;
            border-radius: 2px;
            overflow: hidden;

            .progress {
              height: 100%;
              background: #409EFF;
              border-radius: 2px;
              transition: width 0.3s ease;
            }
          }
        }
      }
    }
  }

  // 中间区域样式
  .middle-section {
    .project-card, .weather-card {
      background: white;
      border-radius: 12px;
      padding: 24px;
      height: 380px; // 统一高度
      box-shadow: 0 2px 12px rgba(0,0,0,0.05);
      display: flex;
      flex-direction: column;

      .card-header {
        flex-shrink: 0; // 防止头部被压缩
        margin-bottom: 15px;
      }

      .project-content, .weather-content {
        flex: 1;
        overflow-y: auto;

        &::-webkit-scrollbar {
          width: 6px;
        }

        &::-webkit-scrollbar-thumb {
          background: #dcdfe6;
          border-radius: 3px;
        }

        &::-webkit-scrollbar-track {
          background: #f5f7fa;
        }
      }
    }

    .project-card {
      .project-name {
        margin: 0 0 16px;
        font-size: 24px;
        color: #303133;
      }

      .project-desc {
        color: #606266;
        line-height: 1.8;
        margin-bottom: 24px;
      }

      .project-details {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 20px;

        .detail-item {
          .label {
            color: #909399;
            margin-right: 8px;
          }

          .value {
            color: #303133;
            font-weight: 500;
          }
        }
      }
    }

    .weather-card {
      .weather-content {
        display: flex;
        flex-direction: column;
        height: calc(100% - 50px);

        .weather-main {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 20px 0;
          border-bottom: 1px solid #f0f0f0;
          flex-shrink: 0;

          .weather-icon {
            text-align: center;
            display: flex;
            align-items: center;
            gap: 10px;

            i {
              font-size: 36px;
              color: #409EFF;
            }

            .weather-type {
              font-size: 15px;
              color: #606266;
            }
          }

          .temp-info {
            display: flex;
            align-items: baseline;

            .temperature {
              font-size: 36px;
              font-weight: bold;
              color: #303133;
              line-height: 1;
            }

            .degree {
              font-size: 18px;
              color: #606266;
              margin-left: 2px;
            }
          }
        }

        .weather-details {
          background: #f8f9fa;
          border-radius: 8px;
          padding: 15px;
          margin: 15px 0;
          flex: 1;

          .detail-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 15px;

            &:last-child {
              margin-bottom: 0;
            }

            .detail-item {
              flex: 1;
              display: flex;
              align-items: center;

              i {
                font-size: 18px;
                color: #409EFF;
                margin-right: 8px;
              }

              .detail-text {
                display: flex;
                flex-direction: column;

                .label {
                  font-size: 12px;
                  color: #909399;
                  margin-bottom: 2px;
                }

                .value {
                  font-size: 13px;
                  color: #606266;
                  font-weight: 500;
                }
              }
            }
          }
        }
      }

      .card-header {
        padding-bottom: 12px; // 减小header底部间距
        margin-bottom: 12px;

        .refresh-time {
          color: #909399;
          font-size: 12px;
          margin-left: 5px;
        }
      }
    }
  }

  // 底部区域样式
  .bottom-section {
    .quick-menu-card, .map-card {
      background: white;
      border-radius: 12px;
      padding: 24px;
      height: 360px; // 统一高度
      box-shadow: 0 2px 12px rgba(0,0,0,0.05);
      display: flex;
      flex-direction: column;

      .card-header {
        flex-shrink: 0;
      }

      .menu-grid, .map-container {
        flex: 1;
        overflow: hidden; // 防止内容溢出
      }
    }

    .map-container {
      border-radius: 8px;
      margin-top: 0; // 移之前的边距
    }
  }
}

// 通用卡片头部样式
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;

  h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }
}

// 响应式调整
@media screen and (max-width: 1400px) {
  .middle-section {
    .project-card, .weather-card {
      height: 420px; // 较小屏幕时增加高度
    }
  }
}

@media screen and (max-width: 768px) {
  .home-container {
    padding: 10px;
  }

  .statistics-section {
    .stat-card {
      height: 120px; // 移动端减小高度
    }
  }

  .middle-section, .bottom-section {
    .project-card, .weather-card, .quick-menu-card, .map-card {
      height: auto; // 移动端自适应高度
      min-height: 300px;
    }
  }
}

.bottom-section {
  .quick-menu-card {
    background: white;
    border-radius: 12px;
    padding: 24px;
    height: 360px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);

    .menu-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;
      padding: 10px;

      .menu-item {
        background: #f8f9fa;
        border-radius: 12px;
        padding: 24px;
        text-align: center;
        cursor: pointer;
        transition: all 0.3s ease;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;

        &:hover {
          background: #ecf5ff;
          transform: translateY(-5px);
          box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);

          i {
            color: #409EFF;
          }

          span {
            color: #409EFF;
          }
        }

        i {
          font-size: 32px;
          color: #606266;
          margin-bottom: 12px;
          transition: all 0.3s ease;
        }

        span {
          font-size: 16px;
          color: #606266;
          transition: all 0.3s ease;
          font-weight: 500;
        }
      }
    }
  }

  // 确保地图卡片和快捷菜单卡片高度一致
  .map-card {
    height: 360px;
  }
}

// 响应式调整
@media screen and (max-width: 1200px) {
  .bottom-section {
    .quick-menu-card {
      .menu-grid {
        gap: 15px;

        .menu-item {
          padding: 20px;

          i {
            font-size: 28px;
          }

          span {
            font-size: 14px;
          }
        }
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .bottom-section {
    .quick-menu-card {
      .menu-grid {
        grid-template-columns: repeat(2, 1fr);
        gap: 10px;
      }
    }
  }
}

// 高德地图标记样式
:deep(.amap-marker) {
  .custom-marker {
    position: absolute;
    transform: translate(-50%, -50%);

    .marker-icon {
      transition: all 0.3s ease;

      &:hover {
        transform: scale(1.1);
        box-shadow: 0 6px 16px rgba(37, 99, 235, 0.4);
      }
    }
  }
}

// 信息窗体样式
:deep(.amap-info-content) {
  padding: 0;
  background: none;
  border: none;
  box-shadow: none;

  .map-info-window {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .info-header {
      padding: 12px 16px;
      background: #f5f7fa;

      .project-name {
        font-size: 16px;
        font-weight: 500;
        color: #303133;
        margin-bottom: 8px;
      }

      .project-status {
        display: flex;
        align-items: center;
        gap: 6px;
        color: #67C23A;
        font-size: 13px;

        i {
          font-size: 14px;
        }
      }
    }
  }
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(103, 194, 58, 0.5);
  }
  70% {
    box-shadow: 0 0 0 20px rgba(103, 194, 58, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(103, 194, 58, 0);
  }
}

// 优化地图容器
.map-container {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: inset 0 0 16px rgba(0, 0, 0, 0.1);
  position: relative;

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    box-shadow: inset 0 0 24px rgba(0, 0, 0, 0.08);
    border-radius: 16px;
  }
}
</style>
