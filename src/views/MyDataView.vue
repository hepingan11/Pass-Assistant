<template>
  <div class="body">
    <div class="container">
      <div id="main" style="width: 1200px;height: auto">
        <h1>历史对话数据</h1>
        <el-table :data="list" border style="width: 100%;background-color: var(--bgColor1)"
                  :row-style="{
        height: '100%',
        background: ' var(--bgColor2)',
        border: 'none',
      }"
                  :header-cell-style="{
        background: ' var(--bgColor1)',
        borderColor: 'var(--borderColor)',
      }">
          <el-table-column prop="content" label="问题" width="400px">
            <template #default="scope">
              <div class="content-cell">
                <div :class="['text-content', { 'text-truncated': !scope.row.showContent }]">
                  {{ scope.row.content }}
                </div>
                <el-button 
                  v-if="scope.row.content.length > 100"
                  type="primary" 
                  link
                  @click="toggleContent(scope.row)"
                >
                  <el-icon>
                    <ArrowDown v-if="!scope.row.showContent"/>
                    <ArrowUp v-else/>
                  </el-icon>
                  {{ scope.row.showContent ? '收起' : '展开' }}
                </el-button>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="message" label="输出" width="600px">
            <template #default="scope">
              <div class="content-cell">
                <div :class="['text-content', { 'text-truncated': !scope.row.showMessage }]">
                  {{ scope.row.message }}
                </div>
                <el-button 
                  v-if="scope.row.message.length > 100"
                  type="primary" 
                  link
                  @click="toggleMessage(scope.row)"
                >
                  <el-icon>
                    <ArrowDown v-if="!scope.row.showMessage"/>
                    <ArrowUp v-else/>
                  </el-icon>
                  {{ scope.row.showMessage ? '收起' : '展开' }}
                </el-button>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="createdTime" label="问答时间" width="200px" />
        </el-table>
      </div>
      <el-pagination background layout="prev, pager, next" :total=PageNo @current-change="handleCurrentChange"/>
    </div>
    <div style="display: flex;align-items: center;flex-direction: column;">
      <h2 style="margin-left: 10px">问题频率查看</h2>
      <div style="display: flex;align-items: center;flex-direction: row;">
        <el-button @click="getFrequency(7)" color="var(--themeColor2)">按月查看</el-button>
        <el-button @click="getFrequency(10)" color="var(--themeColor2)">按日查看</el-button>
      </div>

      <e-charts class="chart" :option="option" style="width: 80%" v-if="showTime"/>

      <div>
        <h2>问题高频词查看</h2>
        <el-button @click="getContentCloud" color="var(--themeColor2)">获取</el-button>
        <br>
        <br>
<!--        <e-charts class="chart" :option="contentOption" style="width: 70%"/>-->
        <el-table :data="contentWordMap" border style="width: 100%;background-color: var(--bgColor1)"
                  :row-style="{
        height: '100%',
        background: ' var(--bgColor2)',
        border: 'none',
      }"
                  :header-cell-style="{
        background: ' var(--bgColor1)',
        borderColor: 'var(--borderColor)',
      }">
          <el-table-column prop="name" label="word" width="200px" />
          <el-table-column prop="value" label="频率" width="200px" />
        </el-table>


        <h2>回答高频词查看</h2>
        <el-button @click="getMessageCloud" color="var(--themeColor2)">获取</el-button>
        <br>
        <br>
        <el-table :data="messageWordMap" border style="width: 100%;background-color: var(--bgColor1)"
                  :row-style="{
        height: '100%',
        background: ' var(--bgColor2)',
        border: 'none',
      }"
                  :header-cell-style="{
        background: ' var(--bgColor1)',
        borderColor: 'var(--borderColor)',
      }">
          <el-table-column prop="name" label="word" width="200px" />
          <el-table-column prop="value" label="频率" width="200px" />
        </el-table>
<!--        <e-charts class="chart" :option="messageOption" style="width: 70%"/>-->
      </div>
    </div>
  </div>


</template>

<script setup>
import { ref, onMounted } from 'vue';
import { GetDigitalData, GetContentCloud, GetMessageCloud } from "../../api/BSideApi";

const data = ref()
const PageNo = ref()
const list = ref([])

onMounted(async () => {
  data.value = await GetDigitalData(1,1);
  // 为每个列表项添加展开状态
  list.value = data.value.slice(0, 10).map(item => ({
    ...item,
    showContent: false,
    showMessage: false
  }));
  PageNo.value = data.value.length;
})

const handleCurrentChange = async (pageNum) => {
  // 为新的页面数据也添加展开状态
  list.value = data.value.slice((pageNum-1)*10, (pageNum-1)*10+10).map(item => ({
    ...item,
    showContent: false,
    showMessage: false
  }));
}

// 添加切换显示的方法
const toggleContent = (row) => {
  row.showContent = !row.showContent;
}

const toggleMessage = (row) => {
  row.showMessage = !row.showMessage;
}

const timeListNo = ref([])
const timeList = ref([])
const showTime = ref(false)

function getFrequency(value){
  showTime.value = true
  timeList.value.length = 0;
  timeListNo.value.length = 0;
  let v = 0;
  timeList.value[0] = data.value[0].createdTime.substring(0, value)
  timeListNo.value[0] =1;
  for(let i=1;i<data.value.length;i++){
    if (timeList.value[v] !== data.value[i].createdTime.substring(0, value)){
      timeList.value.push(data.value[i].createdTime.substring(0,value));
      v++;
      timeListNo.value[v]=1;
    }else {
      timeListNo.value[v]++;
    }
  }
}

const contentWordMap = ref()
const messageWordMap = ref()

async function getContentCloud() {
  contentWordMap.value = await GetContentCloud()
}

async function getMessageCloud() {
  messageWordMap.value = await GetMessageCloud()
}

const option = {
  xAxis: {
    type: 'category',
    data: timeList.value
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      data: timeListNo.value,
      type: 'line'
    }
  ]
};

const messageOption = {
  series: [{
    type: 'wordCloud',
    shape: 'circle', // 词云的形状，可以是'circle', 'cardioid', 'diamond', 'triangle', 'triangle-forward', 'pentagon', 'star'
    left: 'center',
    top: 'center',
    width: '70%',
    height: '80%',
    right: null,
    bottom: null,
    sizeRange: [12, 60], // 字体大小范围
    rotationRange: [-90, 90], // 旋转范围
    rotationStep: 45, // 旋转步长
    gridSize: 8, // 网格大小，即词之间的距离
    drawOutOfBound: false,
    textStyle: {
      normal: {
        fontFamily: 'sans-serif',
        fontWeight: 'bold',
        // Color can be a callback function or a color string
        color: function () {
          // Random color
          return 'rgb(' + [
            Math.round(Math.random() * 160),
            Math.round(Math.random() * 160),
            Math.round(Math.random() * 160)
          ].join(',') + ')';
        }
      },
      emphasis: {
        shadowBlur: 10,
        shadowColor: '#333'
      }
    },
    data: [
      {name: '数据', value: 1000},
      {name: '可视化', value: 618},
      {name: '前端', value: 438},
      {name: 'Java', value: 1438},
      {name: '你好', value: 438},
      {name: '程序', value: 238},
      {name: 'Python', value: 738},
    ]
  }]
}

</script>

<style lang="scss" scoped>
.container {
  display: flex;
  text-align: center;
  align-items: center;
  //按列排
  flex-direction: column;
  animation: gradient 8s ease infinite;
}

@keyframes gradient {
  0% {
    background-position: 0 12%;
  }

  50% {
    background-position: 100% 100%;
  }

  100% {
    background-position: 0 12%;
  }
}

#id {
  margin: 0;
}

.chart {
  height: 400px;
}

.body{
  margin: 0;
  height: 100%;
  text-align: center;
  animation: explainAnimation 0.3s;
  box-sizing: border-box;
  overflow-y: auto;
}

.content-cell {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.text-content {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.5;
  transition: all 0.3s ease;
}

.text-truncated {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  max-height: 4.5em;
}

/* 只针对内容展开按钮应用特殊样式 */
.content-cell :deep(.el-button) {
  padding: 4px 0;
  height: auto;
  align-self: flex-start;
}

.content-cell :deep(.el-icon) {
  margin-right: 4px;
}
</style>