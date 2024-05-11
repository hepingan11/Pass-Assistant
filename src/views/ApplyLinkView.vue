<template>
  <div class="body">
    <div class="container">
      <div id="app">
        <div class="left">
          <el-button @click="this.$router.go(-1)" color="#8166e7" style="margin-top: 10px">返回</el-button>
          <span class="text-header">链接名称</span>
          <el-input v-model="form.name" style="width: 240px" placeholder="Pass Assistant" /><br>
          <span class="text-header">链接地址</span>
          <el-input v-model="form.link" style="width: 240px" placeholder="https://...." /><br>
          <span class="text-header">链接简介</span>
          <el-input v-model="form.intro" autosize style="width: 240px" type="textarea"  placeholder="一个AIGC全能网站" /><br>
          <span class="text-header">链接封面</span>
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
        </div>
      </div>
    </div>
  </div>
</template>


<script>
import store from "@/store";
import {onMounted, ref} from "vue";
import {ElNotification} from "element-plus";
import {GetUserLinkList} from "../../api/BSideApi";

export default {
  name: "ApplyLinkView",
  computed: {
    store() {
      return store;
    },
  },
  setup() {
    const form = ref({
      link_name: '',
      link_url: '',
      link_intro: ''
    })

    const tempFile = ref('')

    const userLinkList = ref([])

    onMounted(() => {
      if (store.getters.userinfo) {
        getUserLinkList()
      }
    })

    async function getUserLinkList(){
      let newVar = await GetUserLinkList();
      if (newVar.length>0){
        userLinkList.value = newVar;
      }
    }

    function onChange(e) {
      console.log(e.raw.type)
      if (e.raw.type === 'image/jpg' || e.raw.type === 'image/png' || e.raw.type === 'image/jpeg') {
        if (e.raw.size / 1024 / 1024 > 2) {
          ElNotification({
            title: "错误",
            message: '图片大小不得超过2MB',
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

    function applyLink(){
      let value = form.value;
      if (!value.link_url){
        ElNotification({
          title: "错误",
          message: '请设置链接地址',
          type: "error",
        });
        return
      }
      if (!value.link_name){
        ElNotification({
          title: "错误",
          message: '请设置链接名称',
          type: "error",
        });
        return
      }
      if (!value.link_intro){
        ElNotification({
          title: "错误",
          message: '请设置链接简介',
          type: "error",
        });
        return
      }
      if (!tempFile.value){
        ElNotification({
          title: "错误",
          message: '请设置链接封面图',
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
    }

    return{
      form,
      tempFile,
      onChange,
      applyLink,
      userLinkList,
      getUserLinkList
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
  flex: 1;
}

.right {
  flex: 1;
}
</style>
