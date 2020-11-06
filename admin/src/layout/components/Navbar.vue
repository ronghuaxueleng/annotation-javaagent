<template>
  <div>
    <div class="navbar">
      <div id="navs">
        <span
          v-for="(item,i) in navList"
          :key="i"
          class="nav"
          :class="[isCurrent(item)>0?'active':'']"
          :index="item.name"
          @click="handleSelect(item)"
        >{{ item.navItem }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  data() {
    return {
      navList: [
        { name: '/index', navItem: '参数配置' },
        { name: '/code', navItem: '代码生成器' }
      ]
    }
  },
  computed: {
    // 当前菜单是否激活
    currentPath() {
      return this.$route.path
    }
  },
  methods: {
    isCurrent(item) {
      return this.currentPath.indexOf(item.name) !== -1
    },
    handleSelect(item) {
      this.$router.push({
        path: item.name
      })
    },
    async logout() {
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
    }
  }
}
</script>

<style lang="scss">
  .navbar {
    height: 65px;
    overflow: hidden;
    position: relative;
    background: #000000;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.06);

    #navs {
      height: 65px;
      line-height: 65px;
      margin-left: 175px;
      vertical-align: middle;
      display: inline-block;

      .nav {
        height: 65px;
        font-size: 14px;
        font-family: PingFangSC-Regular, PingFang SC, serif;
        font-weight: 400;
        color: rgba(255, 255, 255, 1);
        margin-left: 36px;
        cursor: pointer;
        display: inline-block;
      }

      .active {
        color: #37B6AF;
        border-bottom: 2px solid #37B6AF;
      }
    }
  }
</style>
