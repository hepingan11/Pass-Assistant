<template>
  <div class="body">
    <el-upload
        drag
        multiple
        class="upload-demo"
        :on-change="onChange"
        list-type="picture"
    >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">
        Drop file here or <em>click to upload</em>
      </div>
      <template #tip>
        <div class="el-upload__tip">
          jpg/png files with a size less than 500kb
        </div>
      </template>
    </el-upload>

    <el-button type="primary" @click="uploadFile" color="var(--themeColor2)" style="margin-left: 50%">上传</el-button>
    <el-divider/>
    <div class="container">
      <span v-for="item in tempFileList" :key="item" class="img-part">
        <div class="item">
          <img v-if="item" :src="item" class="img"/>
        </div>
      </span>
    </div>
  </div>
</template>

<script>
import store from "@/store";
import {ElNotification} from "element-plus";
import {ref} from "vue";

export default({
  name: "PhotoView",
  computed: {
    store() {
      return store;
    },
  },
  setup(){
    const tempFileList = ref([])


    function onChange(e) {
      console.log(e.raw.type)
      if (e.raw.type === 'image/jpg' || e.raw.type === 'image/png' || e.raw.type === 'image/jpeg') {
        if (e.raw.size / 1024 / 1024 > 5) {
          ElNotification({
            title: "错误",
            message: '图片大小不得超过5MB',
            type: "error",
          });
          return false
        }
        new Promise((resolve, reject) => {
          const reader = new FileReader();
          reader.onload = (event) => {
            tempFileList.value.push(event.target.result);
            // tempFileList.value.pop();
            console.log(e)
            console.log(tempFileList)
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

    async function uploadFile() {
      const v = await UploadFile(tempFileList.value)
    }

    return {
      onChange,
      tempFileList,
      uploadFile,
    }
  }
})
</script>

<style scoped>
.app {
  display: flex;
  height: 100%;
  width: 100%;
  background-color: var(--bgColor2);
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

.container {
  display: flex;
  flex-direction: column;
}

.img-part {
  margin-top: 20px;
  display: flex;
  //width: 80%;
  //height: 120px;
  background-color: #7c7c7c;
  border-radius: 10px;
  flex-wrap: nowrap; /* 不允许子元素换行，因为已经在计算属性中处理了换行 */
}

.item {
  margin: 5px;
  flex-shrink: 0; /* 防止子元素缩小，确保每个元素占据固定宽度 */
  width: calc(100% / 3 - 10px); /* 根据每行元素数量动态计算宽度，减去margin和border */
}

.img {
  margin-top: 10px;
  height: 100px;
  width: auto;
}


</style>
