<template>
  <transition name="move">
    <div v-show="showFlag" class="food">
      <div class="food-content">
        <div class="image-header">
          <img :src="food.image">
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
  export default {
    name: 'food',
    props: {
      food: {
        type: Object,
        default() {
          return {};
        }
      }
    },
    data() {
      return {
        showFlag: false
      };
    },
    methods: {
      show() {
        this.showFlag = true;
      }
    }
  };
</script>

<style lang="stylus" ref="StyleSheet/stylus">
  .food
    position: fixed
    top: 0
    left: 0
    bottom: 48px
    width: 100%
    z-index: 30
    background: #fff
    &.move-enter-active, &.move-leave-active
      transition: all 0.2s ease
    &.move-enter, &.move-leave-to
      transform: translate3d(100%, 0, 0)
    .image-header
      position: relative
      width: 100%
      height:0
      padding-top :100%
      img
        position: absolute
        top:0
        left:0
        width: 100%
        height:100%
</style>
