<template>
  <div class="body">
    <div class="upload">
      <!-- 上传组件 -->
      <el-upload
          class="upload-demo"
          drag
          multiple
          ref="uploadRef"
          :action="null"
          :on-remove="removeImg"
        :auto-upload="false"
        :on-change="handleChange"
        :file-list="fileList"
        list-type="picture-card"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽到此 或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip" style="margin-top: 57px">
            只能上传图片且小于5MB
          </div>
        </template>
      </el-upload>

      <!-- 上传按钮 -->
      <el-button @click="handleUpload" color="var(--themeColor1)" style="margin-top: 40px">上传所有图片</el-button>
    </div>
    <div class="imgList">
      <div v-for="item in imgList" :key="item" style="margin-top: 10px">
        <el-image :src=imgLink+item.url class="img-item" lazy :preview-src-list="[imgLink+item.url]"></el-image>
        <el-button class="preview-button" color="var(--themeColor1)" @click="copyUrl(item.url)"><el-icon><Document /></el-icon></el-button>
        <el-button class="delete-button" color="var(--themeColor1)" @click="deleteImg(item.photoId)"><el-icon><DeleteFilled /></el-icon></el-button>
      </div>
    </div>
    <p style="margin-top: 200px;color: #7c7c7c;font-size: 10px">图床由阿里云oss支持，防刷所以一个人最多只能存储20张~</p>
  </div>
  <el-dialog v-model="dialog" title="确认删除？" width="400">
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialog = false">Cancel</el-button>
        <el-button type="primary" @click="deleteImgReal" color="var(--themeColor1)">
          Yes
        </el-button>
      </div>
    </template>
  </el-dialog>
  <LoginDialog :show="loginVisible" @close="loginVisible = false"/>
</template>

<script setup>
import {ElLoading, ElMessage, ElNotification} from "element-plus";
import {onMounted, ref} from "vue";
import {DeleteImgById, GetUserImgList, UploadFile} from "../../api/BSideApi";
import LoginDialog from "@/components/LoginDialog.vue";
import router from "@/router";


const fileList = ref([])
const uploadRef = ref(null)
const MAX_FILE_SIZE = 5 * 1024 * 1024;
// 处理文件选择事件
const handleChange = (file) => {
  if (file.size > MAX_FILE_SIZE) {
    ElMessage.warning(`文件 ${file.name} 大小超过限制，请选择小于5MB的图片！`)
    return
  }
  if (fileList.value.length === 5) {
    ElMessage.warning("一次性最多只能上传5张(虽然显示还可以上传其实不行了)")
    return
  }
  fileList.value.push(file)
  console.log(fileList)
}
let loginVisible = ref(false);
// 处理上传逻辑
const handleUpload = async () => {
  const loading = ElLoading.service({
    text: '正在上传...',
    spinner: 'el-icon-loading',
    background: 'rgba(0, 0, 0, 0.7)',
  })

  try {
    // 假设这里有一个上传接口
    // 模拟上传请求
    const formData = new FormData();
    // 添加自定义参数到 FormData
    fileList.value.forEach((file) => {
      formData.append('files', file.raw, file.name)
    })
    await UploadFile(formData)

    ElMessage.success('上传成功')
    fileList.value = []  // 清空已上传的文件列表
  } catch (error) {
    ElMessage.error('上传失败')
  } finally {
    loading.close()
  }
  router.go(0);
}
const imgList = ref([])

const dialog = ref(false)
const imgId = ref(null)
function deleteImg(id){
  imgId.value = id
  dialog.value = true
}

async function deleteImgReal() {
  await DeleteImgById(imgId.value);
  dialog.value = false
  ElMessage.success("删除成功")
  router.go(0);
}
const removeImg = (file) =>{
  fileList.value.filter((v) => v === file)
}

const imgLink = ref('')

function copyUrl(url) {
  const v = imgLink.value+url;
  navigator.clipboard.writeText(v);
  ElNotification({
    message: "复制链接成功",
    type: "success",
  });
}

onMounted(async () => {
  imgList.value = await GetUserImgList();
  imgLink.value = process.env.VUE_APP_IMAGE;
})
</script>

<style lang="scss" scoped>

@keyframes explainAnimation {
  from {
    transform: scale(0);
  }

  to {
    transform: scale(1);
  }
}

.body{
  margin: 0;
  height: 100%;
  text-align: center;
  animation: explainAnimation 0.3s;
  box-sizing: border-box;
  overflow-y: auto;
}

.imgList{
  margin-top: 50px;
  position: relative;
}

.img-item{
  border-radius: 10px;
  width: 80%;
  max-width: 500px;
  max-height: 500px;
}

.preview-button{
  position: absolute;
  margin-left: -35px;
  margin-top: 2px;
}

.delete-button{
  position: absolute;
  margin-left: -35px;
  margin-top: 37px;
}
</style>