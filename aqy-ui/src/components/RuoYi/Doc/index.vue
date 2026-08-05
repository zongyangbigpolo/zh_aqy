<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'ProjectDoc',
  data() {
    return {
      url: 'https://github.com/zongyangbigpolo/zh_aqy/blob/main/README.md'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>