<template>
  <div class="app">
    <div class="body">
      <div class="news-content">
        <h1>课表查询</h1>
        <div>
          <el-input v-model="account" placeholder="请输入学号" style="width: 240px;left: 30%;height: 40px"></el-input><br>
          <el-input v-model="password" placeholder="请输入密码" style="width: 240px;left: 30%;height: 40px"></el-input>
          <el-button @click="login" color="var(--themeColor2)" style="margin-top: 120px;margin-left: 20%">登录查询</el-button>
        </div>
        {{classInfo}}
        <h1>派斯新闻</h1>
        <div v-for="item in NewsList" :key="item" class="news-item">
          <div class="news-image">
            <el-image :src="imageUrl+item.img" fit="contain" style="width: 400px;border-radius: 10px;"></el-image>
          </div>
          <div class="news-description">
            <h2>{{ item.title }}</h2>
            <p>{{ item.text}}</p>
            <p style="color: #7c7c7c">{{item.time2}}-{{item.time}}</p>
          </div>
        </div>
        <el-pagination background layout="prev, pager, next" :total="num" @current-change="handleCurrentChange"/>
      </div>
    </div>
  </div>
</template>

<script>
import {onMounted, ref} from "vue";
import {getAllNum, getPassNews, passLogin} from "../../api/BSideApi";
import {ElNotification} from "element-plus";

  export default {
    name: "PassnewsView",
    setup(){
      const num = ref(1000)
      onMounted(async () => {
        ElNotification({
          title: "该模块为我学校的模块，不是本校的可以无视ฅ۶•ﻌ•♡",
          type: "info",
        });
        await GetPassNews();
        num.value = await getAllNum()*10;
      })

      const account = ref('')
      const password = ref('')
      const imageUrl = ref("https://www.paisi.edu.cn/")
      const NewsList = ref([])
      async function GetPassNews() {
        NewsList.value = await getPassNews(1)
      }

      const classInfo = ref("")
      async function login() {
        classInfo.value = await passLogin(account,password)
        if (classInfo.value != null){
          ElNotification({
            title: "查询成功",
            message: 'query success!',
            type: "success",
          });
        }
      }

      async function handleCurrentChange(pageNum) {
        NewsList.value = await getPassNews(pageNum)
      }
      return {
        NewsList,
        imageUrl,
        handleCurrentChange,
        num,
        password,
        account,
        login,
        classInfo
      }
    }
  }
</script>

<style scoped>
.body {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  flex-direction: column;
  align-items: center;
  display: flex;
  overflow: auto;
  background: linear-gradient(to bottom right, var(--bgColor1), var(--bgColor2),var(--bgColor3));
  background-size: 200% 200%;
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

.news-item {
  display: flex;
  width: 100%;
  margin-bottom: 30px;
}

.news-image {
  flex: 1;
}

.news-description {
  flex: 2;
  padding: 0 20px;
}

.news-content{
  display: flex;
  flex-direction: column;
  width: 80%;
}

.app {
  display: flex;
  height: 100%;
  width: 100%;
  background-color: var(--bgColor2);
}
</style>
