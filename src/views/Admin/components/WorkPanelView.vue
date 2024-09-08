<template>
  <div class="body">
    <el-button @click="show = true" color="var(--themeColor1)" style="margin-top: 10px;margin-bottom: 10px">添加作品</el-button>
  </div>
  <el-table
      :data="workList"
      height="95%"
      :header-cell-style="{
        background: ' var(--bgColor1)',
        borderColor: 'var(--borderColor)',
      }"
      style="background-color: var(--bgColor1)"
      :row-style="{
        height: '100%',
        background: ' var(--bgColor1)',
        border: 'none',
      }"
  >
    <el-table-column prop="workId" label="标识" />
    <el-table-column prop="name" label="昵称" />
    <el-table-column prop="url" label="链接" />
    <el-table-column prop="sort" label="类型" />
    <el-table-column prop="updatedTime" label="最后操作时间" />
    <el-table-column prop="createdTime" label="创建时间" />
    <el-table-column fixed="right" label="操作" width="200" style="background-color: var(--bgColor1);">
      <template #header>
        <el-input
            style="width: 180px"
            v-model="prompt"
            size="small"
            placeholder="模糊搜索"
            @change="search"
        />
      </template>
      <template #default="scope">
        <el-button
            link
            type="primary"
            size="small"
            @click="handleUpdateWorkInfo(scope.row)"
        >编辑</el-button
        >
        <el-button
            link
            type="primary"
            size="small"
            @click="handleDeleteWorkInfo(scope.row)"
        >删除</el-button>
      </template>
    </el-table-column>
  </el-table>

  <el-dialog v-model="show" title="Shipping address" width="500" style="background-color: var(--bgColor2)">
    <span>
      名字：<el-input v-model="name"/><br>
      地址：<br><el-input v-model="url"/><br/><br/>
      类型：
      <el-select v-model="sort"
                 placeholder="请选择">
        <el-option
            v-for="item in sortList"
            :key="item"
            :label="item"
            :value="item">
        </el-option>
      </el-select>
    </span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="show = false">取消</el-button>
        <el-button type="primary" @click="addWork">
          添加
        </el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog
      v-model="dialogFormVisible"
      width="400px"
      x
      style="background-color: var(--bgColor1); padding-top: 20px"
  >
    <el-form>
      <el-form-item label="昵称" label-width="100px">
        <el-input
            autocomplete="off"
            tyle="width: 120px"
            v-model="form.name"
        />
      </el-form-item>
      <el-form-item label="url" label-width="100px">
        <el-input
            autocomplete="off"
            tyle="width: 120px"
            v-model="form.url"
        />
      </el-form-item>
      <el-form-item label="类型" label-width="100px">
        <el-input
            autocomplete="off"
            tyle="width: 120px"
            v-model="form.sort"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button color="var(--themeColor2)" @click="onUpdate">
          修改
        </el-button>
      </span>
    </template>
  </el-dialog>

  <el-dialog
      v-model="dialogDeleteVisible"
      width="400px"
      x
      style="background-color: var(--bgColor1); padding-top: 20px"
  >
    <span>确定删除吗？</span>
    <template #footer>
      <span class="dialog-footer">
        <el-button color="var(--themeColor2)" @click="onDelete">
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>

</template>

<script>
  import store from "@/store";
  import {onMounted, ref} from "vue";
  import {AddWork, DeleteWork, GetWorkList, UpdateWork} from "../../../../api/BSideApi";
  import {ElNotification} from "element-plus";

  export default {
    name: "WorkPanelView",
    computed: {
      store() {
        return store;
      },
    },
    setup(){
      const show = ref(false);
      const name = ref('')
      const url = ref('')
      const sort = ref('')
      const sortList = ref([
        "video",
        "spline",
        "other"
      ])
      const workList = ref([])

      const workId = ref()

      onMounted(async () => {
        workList.value = await GetWorkList();
      })

      async function addWork() {
        const v = ref()
        v.value = AddWork(name.value, url.value, sort.value);
        if (v.value) {
          ElNotification({
            title: "添加成功",
            message: '-----',
            type: "success",
          });
        } else {
          ElNotification({
            title: "出问题了",
            message: '-----',
            type: "error",
          });
        }
        show.value = false;
        workList.value = await GetWorkList();
      }

      const form = ref({
        "workId": null,
        "name": '',
        "url": '',
        "sort": '',
        "createdTime": null,
      })
      const dialogFormVisible = ref(false);
      function handleUpdateWorkInfo(row){
        form.value.workId = row.workId;
        form.value.name = row.name;
        form.value.url = row.url;
        form.value.sort = row.sort;
        form.value.createdTime = row.createdTime;
        dialogFormVisible.value = true;
      }

      function search(){

      }

      const dialogDeleteVisible = ref(false);
      function handleDeleteWorkInfo(row){
        workId.value=row.workId;
        dialogDeleteVisible.value = true;
      }

      async function onUpdate() {
        const v = ref();
        v.value = await UpdateWork(form.value);
        if (!v.value){
          ElNotification({
            title: "修改成功",
            message: '-----',
            type: "success",
          });
        }else {
          ElNotification({
            title: "修改失败",
            message: '-----',
            type: "error",
          });
        }
        workList.value = await GetWorkList();
        dialogFormVisible.value =false
      }

      async function onDelete() {
        const v = ref();
        v.value = await DeleteWork(workId.value);
        if (!v.value){
          ElNotification({
            title: "删除成功",
            message: '-----',
            type: "success",
          });
          dialogDeleteVisible.value = false;
          workList.value = await GetWorkList();
        }
      }
      return{
        show,
        addWork,
        name,
        url,
        sort,
        sortList,
        workList,
        handleUpdateWorkInfo,
        search,
        handleDeleteWorkInfo,
        dialogFormVisible,
        dialogDeleteVisible,
        form,
        onUpdate,
        onDelete,
      }
    }
  }
</script>

<style lang="scss" scoped>
.body{
  scroll-behavior: smooth;
  width: 100%;
  flex-direction: column;
  align-items: center;
  display: flex;
  overflow: auto;
}
</style>