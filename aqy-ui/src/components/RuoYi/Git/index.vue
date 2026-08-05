<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'ProjectGit',
  data() {
    return {
      url: 'https://github.com/zongyangbigpolo/zh_aqy'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>