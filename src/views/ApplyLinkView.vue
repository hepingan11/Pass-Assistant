<template>
  <div class="body">
    <div class="container">
      <div id="app">
        <div class="left">
          <el-button @click="this.$router.go(-1)" color="#8166e7" style="margin-top: 10px">返回</el-button>
          <span class="text-header">链接名称</span>
          <el-input v-model="form.linkName" style="width: 220px" placeholder="Pass Assistant"  /><br>
          <span class="text-header">链接地址</span>
          <el-input v-model="form.linkUrl" style="width: 220px" placeholder="https://...." /><br>
          <span class="text-header">链接简介</span>
          <el-input v-model="form.linkIntro" autosize style="width: 220px" type="textarea"  placeholder="一个AIGC全能网站" /><br>
          <span class="text-header">链接分区</span>
          <el-select
              v-model="form.linkSort"
              placeholder="Select"
              size="large"
              style="width: 220px"
          >
            <el-option
                v-for="item in sorts"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
          <span class="text-header">链接封面(推荐16:9的图片)</span>
          <el-upload
              style="
                    background-color: var(--bgColor2);
                    width: 100px;
                    height: 100px;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                  "
              :auto-upload="false"
              :on-change="onChange"
              :show-file-list="false"
          >
            <img v-if="tempFile" :src="tempFile" style="width: 100px;height: 100px"/>
            <el-icon v-else class="avatar-uploader-icon">
              <Plus
              />
            </el-icon>
          </el-upload>
          <br>
          <el-button @click="applyLink" color="#8166e7">提交申请链接</el-button>
        </div>
        <div class="right">
          <h1>我的申请</h1>
          <div v-if="!userLinkList">
            <h1>好像还没有你的申请哦~</h1>
          </div>
          <div class="" >
            <div v-for="item in userLinkList" :key="item" >
              <div class="news-item">
                <div class="news-image">
                  <el-image :src="imageUrl+item.linkImg" fit="contain" style="width: 200px"></el-image>
                </div>
                <div class="news-description">
                  <h2>
                    {{ item.linkName }}
                  </h2>
                  <p>{{ item.linkIntro }}</p>
                  <p>分类:{{ item.linkSort }}</p>
                  <p>
                    <el-button
                        type="primary"
                        size="small"
                        @click="handle(item.linkUrl)"
                        color="var(--themeColor2)"
                    >
                      查看
                    </el-button>
                    <el-button type="primary" size="small" color="var(--themeColor2)" @click="deleteLink(item.linkId)">删除</el-button>
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
import store from "@/store";
import {onMounted, ref} from "vue";
import {ElNotification} from "element-plus";
import {ApplyLink, deleteLinkById, GetUserLinkList} from "../../api/BSideApi";

export default {
  name: "ApplyLinkView",
  computed: {
    store() {
      return store;
    },
  },
  methods: {

  },
  setup() {
    const form = ref({
      linkName: '',
      linkUrl: '',
      linkIntro: '',
      linkSort: '',
      images: ''
    });

    const tempFile = ref('')

    function handle(url){
      window.open(url,'_blank');
    }

    const userLinkList = ref([])

    const sorts = [
      {
        value: 'work',
        label: '资源类',
      },
      {
        value: 'ai',
        label: 'Ai类',
      },
      {
        value: 'tool',
        label: '工具类',
      },
      {
        value: 'tech',
        label: '黑科技',
      },
      {
        value: 'other',
        label: '其它',
      },
    ]

    const imageUrl = ref('')

    function setCss() {
      let flag = navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i)
      if (flag){
        let byClass = document.getElementsByClassName('news-item')[0].style
        byClass.flexDirection = 'column';
        byClass.height = '29vh';
      }
    }

    onMounted(() => {
      imageUrl.value = process.env.VUE_APP_IMAGE;
      if (store.getters.userinfo) {
        getUserLinkList();
        setCss();
        getUserLinkList();
      }
    })


    async function getUserLinkList() {
      userLinkList.value = await GetUserLinkList();
    }


    function onChange(e) {
      console.log(e.raw.type)
      if (e.raw.type === 'image/jpg' || e.raw.type === 'image/png' || e.raw.type === 'image/jpeg') {
        if (e.raw.size / 1024 / 1024 > 4) {
          ElNotification({
            title: "错误",
            message: '图片大小不得超过4MB',
            type: "error",
          });
          return false
        }
        new Promise((resolve, reject) => {
          const reader = new FileReader();
          reader.onload = (event) => {
            tempFile.value = event.target.result
            form.value.images = e.raw
            console.log(e)
            resolve(e);
          };
          reader.onerror = (error) => {
            reject(error);
          };
          reader.readAsDataURL(e.raw);
        });
      } else {
        ElNotification({
          title: "错误",
          message: '请上传正确的图片',
          type: "error",
        });
        return false
      }
    }

    async function applyLink() {
      let value = form.value;
      if (!value.linkUrl) {
        ElNotification({
          title: "错误",
          message: '请设置链接地址',
          type: "error",
        });
        return
      }
      if (!value.linkName) {
        ElNotification({
          title: "错误",
          message: '请设置链接名称',
          type: "error",
        });
        return
      }
      if (!value.linkIntro) {
        ElNotification({
          title: "错误",
          message: '请设置链接简介',
          type: "error",
        });
        return
      }
      if (!tempFile.value) {
        ElNotification({
          title: "错误",
          message: '请设置链接封面图',
          type: "error",
        });
        return
      }
      if (!value.linkSort) {
        ElNotification({
          title: "错误",
          message: '请设置链接分类分区',
          type: "error",
        });
        return
      }
      const formData = new FormData();
      for (const key in form.value) {
        if (form.value[key]) {
          formData.append(key, form.value[key]);
          console.log(key)
        }
      }
      try {
        const promise = await ApplyLink(formData);
        if (!promise){
          ElNotification({
            title: "申请成功",
            message: '请刷新页面查看,可以联系1973016127@qq.com加速申请',
            type: "success",
          });
          location.reload()
        }
      } catch (e) {
        ElNotification({
          title: "错误",
          message: e,
          type: "error",
        });
      }
    }

    async function deleteLink(id) {
      const value = await deleteLinkById(id);
      if (!value){
        ElNotification({
          title: "删除成功",
          message: '请刷新页面查看',
          type: "success",
        });
      }
    }

    return{
      form,
      tempFile,
      onChange,
      applyLink,
      userLinkList,
      sorts,
      handle,
      imageUrl,
      deleteLink,
    }
  }
}

</script>

<style lang="scss" scoped>
:deep(.s_container) {
  overflow: auto;
  height: 100%;
  .el-scrollbar__view {
    transition: all 0.3s;
  }

  @media screen and (min-height: 756px) {
    .el-scrollbar__view {
      padding: 40px;
    }
  }

  @media screen and (max-height: 756px) {
    .el-scrollbar__view {
      padding: 10px;
    }
  }
}

:deep(.u_container) {
  display: flex;
  flex-direction: column;
  .head_model {
    min-height: 110px;
  }
  .el-table tr {
    background: transparent;
  }
}

:deep(.container) {
  .el-table__cell {
    border-bottom: 1px solid var(--borderColor);
  }
  .hover-row {
    .el-table__cell {
      background-color: var(--borderColor);
      color: var(--textColor2);
    }
  }

  .el-table__inner-wrapper::before {
    background-color: var(--borderColor);
  }

  .el-tabs {
    height: 100%;
    display: flex;
    flex-direction: column;

    .el-tabs__content {
      height: 100%;

      .el-tab-pane {
        height: 100%;
        .u_container {
          height: 100%;

          .el-table {
            > .el-table__inner-wrapper {
              height: 100% !important;
            }
          }
        }
      }
    }
  }
}

@keyframes explainAnimation {
  from {
    transform: scale(0);
  }

  to {
    transform: scale(1);
  }
}

:deep(.el-tabs__nav-wrap::after) {
  background: var(--bgColor1);
}

.container {
  animation: explainAnimation 0.3s;
  max-width: 1100px;
  width: 100%;
  box-sizing: border-box;
  padding: 10px 20px 20px;
  margin: 30px 0px;
  height: 90%;
  background-color: var(--bgColor1);
  border-radius: 8px;
}

.body {
  scroll-behavior: smooth;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  flex-direction: column;
  flex: 1;
  align-items: center;
  padding: 0 20px 0px;
  display: flex;
  overflow: auto;
  background-color: var(--bgColor2);
}

::v-deep(.el-tabs__item.is-active) {
  color: var(--textColor1);
}

::v-deep(.el-tabs__item:hover) {
  color: #959595;
}

::v-deep(.el-tabs__active-bar) {
  background-color: var(--themeColor1);
}

::v-deep(.el-tabs__item) {
  color: #626262;
}
.no_data {
  height: 540px;
  margin-top: 10px;
}

.text-header{
  margin-top: 20px;
  margin-bottom: 15px;
  display: flex;
}

#app {
  display: flex;
  width: 100vw;
  flex-direction: row;
}

.left{
  float: left;
  width: 40%;
  height: 100%;
  position: absolute;
  overflow: auto;
}

.right {
  float: right;
  right: 0px;
  width: 50%;
  height: 80%;
  position: absolute;
  overflow: auto;
}

.news-item {
  display: flex;
  flex-direction: row;
  width: 60vh;
  margin-bottom: 20px;
}

.news-image {
  flex: 2;
}

.news-description {
  flex: 2;
  display: flex;
  flex-direction: column;
}

h2 {
  margin-top: -8px;
  margin-bottom: -5px;
  font-size: 1.5rem;
}

p {
  margin-bottom: -10px;
  color: #555;
}
</style>
