<template>
  <div class="body">
    <div class="questions">
      <div class="block">
        <el-avatar shape="circle" :size="200"  >
          <h1>
            {{userinfo.userName}}
          </h1>
        </el-avatar>
        <div>
          <div v-if="editNameStaus === 1">
            <el-input v-model="userinfo.userName" style="width: 240px" placeholder="Please input" />
            <el-button type="primary" color="var(--themeColor2)" @click="savaName(userinfo.userName)">保存</el-button>
          </div>
          <div v-if="editNameStaus === 0">
            <h1>{{userinfo.userName}}</h1>
            <el-button @click="setNameStaus" color="var(--bgcolor2)">
              <el-icon :size="30">
                <Edit />
              </el-icon>
            </el-button>
          </div>


        </div>

        <h2>剩余SUPER币: {{userinfo.frequency}}</h2>
        <el-button @click="router().push({ path: '/Collection' })" type="primary" color="var(--themeColor2)">我的收藏</el-button>
        <br/>
        <el-button @click="getImageList" type="primary" color="var(--themeColor2)">我的绘画</el-button>
        <br/>
        <el-scrollbar height="500px" v-if="showParam === 1">
          <p v-for="item in imageList" :key="item" class="scrollbar-demo-item">
            <el-image class="own-image" :src="imageUrl+item" fit="cover" :preview-src-list="[imageUrl+item]"></el-image>
          </p>
        </el-scrollbar>
      </div>
    </div>
  </div>
</template>


<script>
import store from "@/store";
import {h, onMounted, ref} from "vue";
import {getUserDraw, GetUserInfo, updateUserName} from "../../api/BSideApi";
import router from "@/router";
import {ElNotification} from "element-plus";

export default {
  name: `UserView`,
  methods: {
    router() {
      return router
    }
  },
  computed: {
    store() {
      return store
    }
  },
  setup() {
    const userinfo = ref({
        'userName': "用户",
        "avatar": "",
        "frequency": ""
    });

    const imageList = ref([])

    const showParam = ref(0);

    const imageUrl = ref('');

    const editNameStaus = ref(0)


    onMounted(() => {
      imageUrl.value = process.env.VUE_APP_IMAGE;
      getUser()
    })

    async function getUser() {
      const res = await GetUserInfo();
      store.commit("setUserinfo", res);

      if(res.avatar !=null){
        userinfo.value.avatar = res.avatar;
      }
      if (res.userName !=null){
        userinfo.value.userName= res.userName;
      }
      userinfo.value.frequency= res.frequency;
    }

    async function getImageList() {
      imageList.value= await getUserDraw();
      showParam.value = 1;
    }

    function setNameStaus() {
      editNameStaus.value=1;
    }

    async function savaName(userName) {
      await updateUserName(userName)
      editNameStaus.value = 0;
      ElNotification({
        title: '修改成功',
        message: h('i', {style: 'color: teal'}, 'Success'),
      })
    }


    return {
      userinfo,
      imageUrl,
      showParam,
      imageList,
      getImageList,
      editNameStaus,
      savaName,
      setNameStaus
    }
  }
}
</script>

<style lang="scss" scoped>
@keyframes beating {
  0% {
    transform: translateY(0);
  }

  100% {
    transform: translateY(-10px);
  }
}

@keyframes slideEase {
  0% {
    transform: translateX(-100px);
  }

  100% {
    transform: translateX(0);
  }
}

.body {
  scroll-behavior: smooth;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  flex-direction: column;
  flex: 1;
  align-items: center;
  padding: 0 20px 140px;
  display: flex;
  overflow: auto;
  background-color: var(--bgColor2);
}

.questions {
  max-width: 640px;
  min-width: 350px;
  margin-top: 40px;
}

.block{
  display: grid;
  place-items: center; /* 水平和垂直居中 */
  //margin: auto;
}

.scrollbar-demo-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 190px;
  margin: 12px;
  text-align: center;
  border-radius: 4px;
  background: var(--bgColor2);
  color: var(--el-color-primary);
}

.own-image{
  width: 150px;
  height: 190px;
  border-radius: 8px
}
</style>
