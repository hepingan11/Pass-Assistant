<template>
  <div class="app">
    <div class="nav">
      <el-menu
          default-active="2"
          class="el-menu-vertical-demo"
          :collapse="isCollapse"
          background-color="var(--bgColor2)"
          active-text-color="#e9636c"
          text-color="var(--themeColor2)"
      >
          <el-menu-item index="1">
            <el-icon><HomeFilled /></el-icon>
            <template #title>热门网址</template>
          </el-menu-item>

        <el-menu-item index="2" @click="scrollToTarget">
          <el-icon><StarFilled /></el-icon>
          <template #title>我的收藏</template>
        </el-menu-item>
        <el-menu-item index="3">
          <el-icon><Tickets /></el-icon>
          <template #title>资源~</template>
        </el-menu-item>
        <el-menu-item index="4">
          <el-icon><MagicStick /></el-icon>
          <template #title>AI网站</template>
        </el-menu-item>
        <el-menu-item index="5">
          <el-icon><Box /></el-icon>
          <template #title>工具箱</template>
        </el-menu-item>
        <el-menu-item index="6">
          <el-icon><Cpu /></el-icon>
          <template #title>黑科技</template>
        </el-menu-item>
        <el-menu-item index="7">
          <el-icon><MoreFilled /></el-icon>
          <template #title>其它</template>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="body">
      <el-image :src=topImg class="top-img" fit="cover" @click="router().push({ path: '/apply_view' })"></el-image>
      <br>

      <div class="content">

        <el-icon color="var(--themeColor2)" size="22px"><HomeFilled /></el-icon> 热门网站
        <br><br>
        <div id="hot" class="news-content">
          <div v-for="item in linkList" :key="item" class="news-item">
            <div class="news-image">
              <el-image :src="item.linkImg" fit="contain" style="width: 200px"></el-image>
            </div>
            <div class="news-description">
              <h2>{{ item.linkName }}</h2>
              <p>{{ item.linkIntro}}</p>
            </div>
            <el-button
                type="primary"
                tag="a"
                :href=item.linkUrl
                target="_blank"
                rel="noopener noreferrer"
                style="margin-top: 50px"
                color="var(--themeColor2)"
            >
              >
            </el-button>
          </div>
        </div>

      </div>
    </div>
  </div>

</template>

<script>
  import store from "@/store";
  import {onMounted, ref} from "vue";
  import {getLinkList, getTopImgUrl} from "../../api/BSideApi";
  import {conversionTime} from "@/utils/date";
  import router from "@/router";

  export default {
    name: "LinkView",
    methods: {
      router() {
        return router
      },
      scrollToTarget() {
        this.$refs.targetElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    },
    computed: {
      store() {
        return store;
      },
    },
    mounted() {
      const hash = this.$route.hash;
      if (hash) {
        const targetElement = document.getElementById(hash.substring(1));
        if (targetElement) {
          targetElement.scrollIntoView();
        }
      }
    },
    setup(){
      const dataTables = ref([]);
      const current = ref(1);
      const total = ref(0);
      let load = ref(true);
      let empty = ref(false);
      let error = ref(false);

      const topImg = ref('')

      const linkList = ref([
        {
          linkName: "Pass Assistant",
          linkUrl: "https://gpt.hepingan.top",
          linkIntro: "很厉害的网站哦",
          linkImg: "https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/8330C231A8EF08B924177D874F752187.png",
          linkSort: "hot"
        },
        {
          linkName: "AIGC",
          linkUrl: "https://ai.hepingan.top",
          linkIntro: "balala",
          linkImg: "https://img-hepingan.oss-cn-hangzhou.aliyuncs.com/6B55CC142C9B065BFB5A40A97EA7C732.png",
          linkSort: "hot"
        }
      ])

      const isCollapse = ref(true)

      const count = ref(0)
      const load2 = () => {
        count.value += 2
      }

      async function getTopImg() {
        topImg.value = await getTopImgUrl();
      }

      const hotLinkList = ref([])
      const statLinkList = ref([])
      const workLinkList = ref([])
      const aiLinkList = ref([])
      const toolLinkList = ref([])
      const techLinkList = ref([])
      const otherLinkList = ref([])
      async function LinkList() {
        linkList.value = await getLinkList();
        if (linkList.value) {
          if(linkList.value.every(item=>{
            return item.linkSort = 'hot'
          })){
            hotLinkList.value.
          }
        }
      }

      onMounted( () =>{
        getTopImg();
        LinkList();
      })

      async function handleCurrentChange(pageNum) {
        try {
          let res = await getLinkList(pageNum);
          let records = res.records;
          if (records.length   > 0) {
            for (const r of records) {
              r.createdTime = conversionTime(r.createdTime);
            }
            dataTables.value = records;
          } else {
            empty.value = true;
          }
          current.value = res.current;
          total.value = res.total;
          load.value = false;
          error.value = false;
        } catch (e) {
          load.value = false;
          error.value = true;
        }
      }

      return{
        topImg,
        linkList,
        getTopImg,
        handleCurrentChange,
        dataTables,
        current,
        total,
        load,
        empty,
        error,
        LinkList,
        count,
        load2,
        isCollapse,
        hotLinkList,
        statLinkList,
        workLinkList,
        aiLinkList,
        toolLinkList,
        techLinkList,
        otherLinkList,
      }
    }
  }
</script>

<style lang="scss" scoped>

.app {
  display: flex;
  height: 100%;
  width: 100%;
  background-color: var(--bgColor2);
}

.nav {
  flex: 1;
  max-width: 65px;
  height: 100%;
}

.body {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  flex-direction: column;
  flex: 1;
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

.content{
  margin-bottom: 20px;
}

.top-img{
  width: 85%;
  height: 250px;
  border-radius: 10px;
}

.scrollbar-demo-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
  margin: 10px;
  text-align: center;
  border-radius: 4px;
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.example-pagination-block + .example-pagination-block {
  margin-top: 10px;
}

.news-content{
  display: flex;
  flex-direction: column;
  width: 100%;
}

.news-item {
  display: flex;
  width: 100%;
  margin-bottom: 15px;

}

.news-image {
  flex: 1;
}

.news-description {
  flex: 2;
  padding: 0 20px;
}



h2 {
  margin-bottom: 10px;
  font-size: 1.5rem;
}

p {
  color: #555;
}

.circle-breath {
  background: pink;
  box-shadow: 0 0 0 0 rgb(204, 73, 152);
  height: 36px;
  width: 36px;
  border-radius: 80%;
  animation: donghua 2.4s infinite;
}

@keyframes donghua {
  0% {
    transform: scale(0.60);
    /* 注意rgba中的a的设置 */
    box-shadow: 0 0 0 0 rgba(204, 73, 152, 60%);
  }

  60% {
    transform: scale(1);
    box-shadow: 0 0 0 36px rgba(204, 73, 152, 0%);
  }

  100% {
    transform: scale(0.60);
    box-shadow: 0 0 0 0 rgba(204, 73, 152, 0%);
  }
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}
</style>
