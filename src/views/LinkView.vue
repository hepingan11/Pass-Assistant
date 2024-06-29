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
          <el-menu-item index="1" @click="scrollToHot('hot')">
            <el-icon><HomeFilled /></el-icon>
            <template #title>热门网址</template>
          </el-menu-item>

        <el-menu-item index="2" @click="scrollToHot('stat')">
          <el-icon><StarFilled /></el-icon>
          <template #title>我的收藏</template>
        </el-menu-item>
        <el-menu-item index="3" @click="scrollToHot('work')">
          <el-icon><Tickets /></el-icon>
          <template #title>资源~</template>
        </el-menu-item>
        <el-menu-item index="4" @click="scrollToHot('ai')">
          <el-icon><MagicStick /></el-icon>
          <template #title>AI网站</template>
        </el-menu-item>
        <el-menu-item index="5" @click="scrollToHot('tool')">
          <el-icon><Box /></el-icon>
          <template #title>工具箱</template>
        </el-menu-item>
        <el-menu-item index="6" @click="scrollToHot('tech')">
          <el-icon><Cpu /></el-icon>
          <template #title>黑科技</template>
        </el-menu-item>
        <el-menu-item index="7" @click="scrollToHot('other')">
          <el-icon><MoreFilled /></el-icon>
          <template #title>其它</template>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="body">
      <br>
      <div class="content">
        <el-image :src=topImg class="top-img" fit="cover" @click="router().push({ path: '/apply_view' })"></el-image>
        <br>
        <div style="margin-bottom: 15px">
          <el-icon color="var(--themeColor2)" size="25px"><HomeFilled /></el-icon> <span style="font-size: 20px">热门网站</span>
        </div>
        <br>
        <div id="hot" class="news-content">
          <div v-for="item in linkList" :key="item" class="news-item">
              <div class="news-image" v-if="item.isHot ===1">
                <el-image :src="imageUrl+item.linkImg" fit="contain" style="width: 200px"></el-image>
              </div>
              <div class="news-description" v-if="item.isHot === 1">
                <h2>{{ item.linkName }}</h2>
                <p>{{ item.linkIntro}}</p>
              </div>
              <el-button
                  v-if="item.isHot === 1"
                  type="primary"
                  @click="handle(item.linkUrl)"
                  target="_blank"
                  rel="noopener noreferrer"
                  style="margin-top: 50px"
                  color="var(--themeColor2)"
              >
                GO
              </el-button>
          </div>
        </div>
<!--收藏-->
        <div style="margin-bottom: 15px">
          <el-icon color="var(--themeColor2)" size="25px"><StarFilled /></el-icon> <span style="font-size: 20px">我的收藏</span>
        </div>
        <div id="stat" class="news-content">
          <div v-for="item in userStatLink" :key="item" class="news-item">
            <div class="news-image">
              <el-image :src="imageUrl+item.linkImg" fit="contain" style="width: 200px"></el-image>
            </div>
            <div class="news-description">
              <h2>{{ item.linkName }}</h2>
              <p>{{ item.linkIntro}}</p>
            </div>
            <el-button
                type="primary"
                @click="handle(item.linkUrl)"
                target="_blank"
                rel="noopener noreferrer"
                style="margin-top: 50px"
                color="var(--themeColor2)"
            >
              GO
            </el-button>
            <el-icon v-if="userStatLink.includes(item)" color="var(--themeColor2)" @click="cancelLinkStat(item.linkId)"><StarFilled /></el-icon>
          </div>
        </div>

        <div style="margin-bottom: 15px">
          <el-icon color="var(--themeColor2)" size="25px"><Tickets /></el-icon> <span style="font-size: 20px">冲浪资源</span>
        </div>
        <div id="work" class="news-content">
          <div v-for="item in linkList" :key="item" class="news-item">
            <div class="news-image" v-if="item.linkSort === 'work'">
              <el-image :src="imageUrl+item.linkImg" fit="contain" style="width: 200px"></el-image>
            </div>
            <div class="news-description" v-if="item.linkSort === 'work'">
              <h2>{{ item.linkName }}</h2>
              <p>{{ item.linkIntro}}</p>
            </div>
            <el-button
                v-if="item.linkSort === 'work'"
                type="primary"
                @click="handle(item.linkUrl)"
                target="_blank"
                rel="noopener noreferrer"
                style="margin-top: 50px"
                color="var(--themeColor2)"
            >
              GO
            </el-button>
            <el-icon v-if="item.linkSort === 'work'" @click="addLinkStat(item.linkId)"><StarFilled /></el-icon>
          </div>
        </div>

        <div style="margin-bottom: 15px">
          <el-icon color="var(--themeColor2)" size="25px"><MagicStick /></el-icon> <span style="font-size: 20px">Ai改变未来</span>
        </div>
        <div id="ai" class="news-content">
          <div v-for="item in linkList" :key="item" class="news-item">
            <div class="news-image" v-if="item.linkSort === 'ai'">
              <el-image :src="imageUrl+item.linkImg" fit="contain" style="width: 200px"></el-image>
            </div>
            <div class="news-description" v-if="item.linkSort === 'ai'">
              <h2>{{ item.linkName }}</h2>
              <p>{{ item.linkIntro}}</p>
            </div>

            <el-button
                v-if="item.linkSort === 'ai'"
                type="primary"
                @click="handle(item.linkUrl)"
                target="_blank"
                rel="noopener noreferrer"
                style="margin-top: 50px"
                color="var(--themeColor2)"
            >
              GO
            </el-button>
            <el-icon v-if="item.linkSort === 'ai'" @click="addLinkStat(item.linkId)"><StarFilled /></el-icon>
          </div>
        </div>

        <div style="margin-bottom: 15px">
          <el-icon color="var(--themeColor2)" size="25px"><Box /></el-icon> <span style="font-size: 20px">工具箱</span>
        </div>
        <div id="tool" class="news-content">
          <div v-for="item in linkList" :key="item" class="news-item">
            <div class="news-image" v-if="item.linkSort === 'tool'">
              <el-image :src="imageUrl+item.linkImg" fit="contain" style="width: 200px"></el-image>
            </div>
            <div class="news-description" v-if="item.linkSort === 'tool'">
              <h2>{{ item.linkName }}</h2>
              <p>{{ item.linkIntro}}</p>
            </div>
            <el-button
                v-if="item.linkSort === 'tool'"
                type="primary"
                @click="handle(item.linkUrl)"
                target="_blank"
                rel="noopener noreferrer"
                style="margin-top: 50px"
                color="var(--themeColor2)"
            >
              GO
            </el-button>
            <el-icon v-if="item.linkSort === 'tool'" @click="addLinkStat(item.linkId)"><StarFilled /></el-icon>
          </div>
        </div>

        <div style="margin-bottom: 15px">
          <el-icon color="var(--themeColor2)" size="25px"><Cpu /></el-icon> <span style="font-size: 20px">黑科技</span>
        </div>
        <div id="tech" class="news-content">
          <div v-for="item in linkList" :key="item" class="news-item">
            <div class="news-image" v-if="item.linkSort === 'tech'">
              <el-image :src="imageUrl+item.linkImg" fit="contain" style="width: 200px"></el-image>
            </div>
            <div class="news-description" v-if="item.linkSort === 'tech'">
              <h2>{{ item.linkName }}</h2>
              <p>{{ item.linkIntro}}</p>
            </div>
            <el-button
                v-if="item.linkSort === 'tech'"
                type="primary"
                @click="handle(item.linkUrl)"
                target="_blank"
                rel="noopener noreferrer"
                style="margin-top: 50px"
                color="var(--themeColor2)"
            >
              GO
            </el-button>
            <el-icon v-if="item.linkSort === 'tech'" @click="addLinkStat(item.linkId)"><StarFilled /></el-icon>
          </div>
        </div>

        <div style="margin-bottom: 15px">
          <el-icon color="var(--themeColor2)" size="25px"><MoreFilled /></el-icon> <span style="font-size: 20px">其它</span>
        </div>
        <div id="other" class="news-content">
          <div v-for="item in linkList" :key="item" class="news-item">
            <div class="news-image" v-if="item.linkSort === 'other'">
              <el-image :src="imageUrl+item.linkImg" fit="contain" style="width: 200px"></el-image>
            </div>
            <div class="news-description" v-if="item.linkSort === 'other'">
              <h2>{{ item.linkName }}</h2>
              <p>{{ item.linkIntro}}</p>
            </div>
            <el-button
                v-if="item.linkSort === 'other'"
                type="primary"
                @click="handle(item.linkUrl)"
                target="_blank"
                rel="noopener noreferrer"
                style="margin-top: 50px"
                color="var(--themeColor2)"
            >
              GO
            </el-button>
            <el-icon v-if="item.linkSort === 'other'" @click="addLinkStat(item.linkId)"><StarFilled /></el-icon>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import store from "@/store";
  import {onMounted, ref} from "vue";
  import {addLinkStatById, cancelLinkStatById, getLinkList, getTopImgUrl, selectStatLink} from "../../api/BSideApi";
  import {conversionTime} from "@/utils/date";
  import router from "@/router";
  import {Cpu, StarFilled, Tickets} from "@element-plus/icons-vue";
  import {ElNotification} from "element-plus";

  export default {
    name: "LinkView",
    components: {Cpu, Tickets, StarFilled},
    methods: {
      router() {
        return router
      },
    },
    computed: {
      store() {
        return store;
      },
    },
    mounted() {
      // const hash = this.$route.hash;
      // if (hash) {
      //   const targetElement = document.getElementById(hash.substring(1));
      //   if (targetElement) {
      //     targetElement.scrollIntoView();
      //   }
      // }
    },
    setup(){
      const dataTables = ref([]);
      const current = ref(1);
      const total = ref(0);
      let load = ref(true);
      let empty = ref(false);
      let error = ref(false);
      const topImg = ref('')

      function handle(url){
        window.open(url,'_blank');
      }

      const linkList = ref([])

      function scrollToHot(sort) {
        let hotElement = document.getElementById(sort);
        if (hotElement) {
          hotElement.scrollIntoView({behavior: 'smooth', block: 'start'});
        }
      }
      const isCollapse = ref(true)

      const count = ref(0)
      const load2 = () => {
        count.value += 2
      }

      async function getTopImg() {
        topImg.value = await getTopImgUrl();
      }

      async function LinkList() {
        linkList.value = await getLinkList();
        linkList.value = linkList.value.filter(item => item.isPublic !== 0);

      }

      const imageUrl= ref('');

      onMounted( () =>{
        imageUrl.value = process.env.VUE_APP_IMAGE;
        getTopImg();
        LinkList();
        getUserStatLink();
      })

      const userStatLink = ref([]);

      async function addLinkStat(id) {
        const v = await addLinkStatById(id)
        if (!v) {
          ElNotification({
            title: "收藏成功",
            message: '可以刷新页面查看最新',
            type: "success",
          });
        }
      }

      async function cancelLinkStat(id) {
        const v = await cancelLinkStatById(id);
        if (!v) {
          ElNotification({
            title: "取消收藏成功",
            message: '可以刷新页面查看最新',
            type: "success",
          });
        }
      }

      async function getUserStatLink() {
        userStatLink.value = await selectStatLink();
        console.log(userStatLink)
      }

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
        handle,
        scrollToHot,
        imageUrl,
        userStatLink,
        addLinkStat,
        cancelLinkStat,
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
  width: 100%;
  height: 250px;
  border-radius: 10px;
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
  color: var(--textColor2);
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
