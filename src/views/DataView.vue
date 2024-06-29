<template>
  <div class="body">
    <div class="content">
      <h1>京东数码价格区间占比数量图</h1>
      <el-image :src="priceImage" :preview-src-list="[priceImage]" style="width: 50%" ></el-image><br>
      <h2>评论数排行前三商品</h2>
      <p>{{price.top1}}:{{price.value1}}</p>
      <p>{{price.top2}}:{{price.value2}}</p>
      <p>{{price.top3}}:{{price.value3}}</p>
      <el-button @click="getPrice" color="var(--themeColor2)" size="large">数据有问题？点我重新获取该数据</el-button>
      <h1 style="margin-top: 200px;">京东数码各商品类别数量图</h1>
      <el-image :src="numImage" :preview-src-list="[numImage]" style="width: 80%"></el-image><br>
      <h2>商品数量排行前三的数码类别</h2>
      <p>{{price2.top1}}:{{price2.value1}}</p>
      <p>{{price2.top2}}:{{price2.value2}}</p>
      <p>{{price2.top3}}:{{price2.value3}}</p>
      <el-button @click="getNum" color="var(--themeColor2)" style="margin-bottom: 100px" size="large">数据有问题？点我重新获取该数据</el-button>
    </div>
  </div>
</template>

<script>
  import router from "@/router";
  import {onMounted, ref} from "vue";
  import {getNumData, getPriceData} from "../../api/BSideApi";
  import {ElLoading} from "element-plus";

  export default {
    name: "DataView",
    methods: {
      router() {
        return router
      },
    },
    setup() {
      onMounted( ()=>{
        getPrice();
        getNum();
        openFullScreen2()
      })

      const imgUrl = ref('')
      const price = ref([])
      const price2 = ref([])

      const openFullScreen2 = () => {
        const loading = ElLoading.service({
          lock: true,
          text: 'Loading',
          background: 'rgba(0, 0, 0, 0.7)',
        })
        setTimeout(() => {
          loading.close()
        }, 2000)
      }
      async function getPrice() {
        price.value = await getPriceData();
        priceImage.value = decodeImage(price.value.img);
      }
      async function getNum() {
        price2.value = await getNumData();
        numImage.value = decodeImage(price2.value.img);
      }

      const priceImage = ref('')
      const numImage = ref('')
      function decodeImage(img) {
        const decodedData = atob(img); // 使用atob函数进行base64解码
        const bytes = new Uint8Array(decodedData.length);
        for (let i = 0; i < decodedData.length; i++) {
          bytes[i] = decodedData.charCodeAt(i);
        }
        const blob = new Blob([bytes], { type: 'image/jpeg' }); // 创建Blob对象
        return URL.createObjectURL(blob); // 使用createObjectURL创建Blob URL
      }

      return {
        getPrice,
        price,
        imgUrl,
        priceImage,
        numImage,
        price2,
        getNum,
      }
    }
  }
</script>

<style lang="scss" scoped>
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

.content{
  text-align: center;
}
</style>
