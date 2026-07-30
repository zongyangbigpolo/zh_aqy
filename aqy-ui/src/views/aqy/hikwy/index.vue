<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="设备地址编号" prop="deviceAddr">
        <el-input
          v-model="queryParams.deviceAddr"
          placeholder="请输入设备地址编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="记录时间" prop="timeFrame">
        <el-date-picker
          v-model="queryParams.timeFrame"
          type="datetimerange"
          value-format="yyyy-MM-dd HH:mm:ss"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期">
        </el-date-picker>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:wy:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:wy:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:wy:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:wy:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
      <el-tab-pane label="数据表格" name="first">
        <el-table v-loading="loading" :data="wyList" :max-height="tableHeight" @selection-change="handleSelectionChange" border>
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="序号" align="center" type="index" />
          <el-table-column label="设备地址编号" align="center" prop="deviceAddr" />
          <el-table-column label="X轴位移(mm)" align="center" prop="valueWyX" />
          <el-table-column label="Y轴位移(mm)" align="center" prop="valueWyY" />
          <el-table-column label="Z轴位移(mm)" align="center" prop="valueWyZ" />
          <el-table-column label="记录时间" align="center" prop="catchTime" width="180">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.catchTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </el-tab-pane>

      <el-tab-pane label="统计图表" name="second">
        <div ref="chart" class="chart-container"></div>
      </el-tab-pane>
    </el-tabs>

    <!-- 添加或修改海康位移数据对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="设备地址编号" prop="deviceAddr">
          <el-input v-model="form.deviceAddr" placeholder="请输入设备地址编号" />
        </el-form-item>
        <el-form-item label="X轴位移(mm)" prop="valueWyX">
          <el-input v-model="form.valueWyX" placeholder="请输入X轴位移(mm)" />
        </el-form-item>
        <el-form-item label="Y轴位移(mm)" prop="valueWyY">
          <el-input v-model="form.valueWyY" placeholder="请输入Y轴位移(mm)" />
        </el-form-item>
        <el-form-item label="Z轴位移(mm)" prop="valueWyZ">
          <el-input v-model="form.valueWyZ" placeholder="请输入Z轴位移(mm)" />
        </el-form-item>
        <el-form-item label="记录时间" prop="catchTime">
          <el-date-picker clearable
            v-model="form.catchTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择记录时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="${comment}" prop="createUid">
          <el-input v-model="form.createUid" placeholder="请输入${comment}" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listWy, getWy, delWy, addWy, updateWy } from "@/api/aqy/hikwy";
import * as echarts from 'echarts';
import moment from 'moment';
import {
  TitleComponent,
  ToolboxComponent,
  TooltipComponent,
  GridComponent,
  DataZoomComponent
} from 'echarts/components';
import { LineChart } from 'echarts/charts';
import { UniversalTransition } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';

echarts.use([
  TitleComponent,
  ToolboxComponent,
  TooltipComponent,
  GridComponent,
  DataZoomComponent,
  LineChart,
  CanvasRenderer,
  UniversalTransition
]);

export default {
  name: "Wy",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 海康位移数据表格数据
      wyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        deviceAddr: null,
        valueWyX: null,
        valueWyY: null,
        valueWyZ: null,
        timeFrame: [moment().subtract(1, "months").format('YYYY-MM-DD HH:mm:ss'), moment().format('YYYY-MM-DD HH:mm:ss')]
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      // 添加图表相关属性
      activeName: 'first',
      myChart: null,
      chartType: 'line',
      tableHeight: document.documentElement.scrollHeight - 330,
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询海康位移数据列表 */
    getList() {
      this.loading = true;
      listWy(this.queryParams).then(response => {
        this.wyList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        deviceAddr: null,
        valueWyX: null,
        valueWyY: null,
        valueWyZ: null,
        catchTime: null,
        createTime: null,
        createUid: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加海康位移数据";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getWy(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改海康位移数据";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateWy(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addWy(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除海康位移数据编号为"' + ids + '"的数据项？').then(function() {
        return delWy(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/wy/export', {
        ...this.queryParams
      }, `wy_${new Date().getTime()}.xlsx`)
    },
    // 添加图表相关方法
    handleClick(tab) {
      if (tab.name === 'second') {
        this.$nextTick(() => {
          if (this.myChart) {
            this.myChart.resize();
          }
          this.renderChart();
        });
      }
    },
    renderChart() {
      const chartDom = this.$refs.chart;
      if (!chartDom) return;

      if (this.myChart) {
        this.myChart.dispose();
      }

      this.myChart = echarts.init(chartDom);
      const xAxisData = this.getXAxisData();
      const seriesData = this.getSeriesData();

      let option = {
        title: {
          text: '位移数据统计图表',
          left: 'center',
          textStyle: {
            color: '#fff',
            fontSize: 18,
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: ['X轴位移(mm)', 'Y轴位移(mm)', 'Z轴位移(mm)'],
          textStyle: {
            color: '#fff'
          },
          top: '5%'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: xAxisData,
          axisLabel: {
            rotate: 45,
            textStyle: {
              color: '#fff'
            }
          },
          axisLine: {
            lineStyle: {
              color: '#fff'
            }
          }
        },
        yAxis: {
          type: 'value',
          scale: true,
          axisLabel: {
            textStyle: {
              color: '#fff'
            }
          },
          axisLine: {
            lineStyle: {
              color: '#fff'
            }
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.1)'
            }
          }
        },
        dataZoom: [
          {
            type: 'slider',
            show: true,
            xAxisIndex: [0],
            start: 0,
            end: 100,
            textStyle: {
              color: '#fff'
            }
          },
          {
            type: 'inside',
            xAxisIndex: [0]
          }
        ],
        series: seriesData
      };

      this.myChart.setOption(option);
    },
    getXAxisData() {
      return [...new Set(this.wyList.map(item =>
        this.parseTime(item.catchTime, '{y}-{m}-{d} {h}:{i}:{s}')
      ))].sort((a, b) => new Date(a) - new Date(b));
    },
    getSeriesData() {
      const xAxisData = this.getXAxisData();

      const wyXData = xAxisData.map(time => {
        const item = this.wyList.find(item =>
          this.parseTime(item.catchTime, '{y}-{m}-{d} {h}:{i}:{s}') === time
        );
        return item ? Number(item.valueWyX) : null;
      });

      const wyYData = xAxisData.map(time => {
        const item = this.wyList.find(item =>
          this.parseTime(item.catchTime, '{y}-{m}-{d} {h}:{i}:{s}') === time
        );
        return item ? Number(item.valueWyY) : null;
      });

      const wyZData = xAxisData.map(time => {
        const item = this.wyList.find(item =>
          this.parseTime(item.catchTime, '{y}-{m}-{d} {h}:{i}:{s}') === time
        );
        return item ? Number(item.valueWyZ) : null;
      });

      return [
        {
          name: 'X轴位移(mm)',
          type: this.chartType,
          data: wyXData,
          smooth: true,
          showSymbol: false,
          lineStyle: {
            width: 2
          }
        },
        {
          name: 'Y轴位移(mm)',
          type: this.chartType,
          data: wyYData,
          smooth: true,
          showSymbol: false,
          lineStyle: {
            width: 2
          }
        },
        {
          name: 'Z轴位移(mm)',
          type: this.chartType,
          data: wyZData,
          smooth: true,
          showSymbol: false,
          lineStyle: {
            width: 2
          }
        }
      ];
    }
  }
};
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: 600px;
  background: #2c3e50;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.2);
}
</style>
