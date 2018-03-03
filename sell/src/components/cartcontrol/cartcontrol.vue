<template>
  <div class="cartcontrol">
    <transition name="move">
      <div class="cart-decrease" v-show="food.count>0" @click="decreaseCart">
        <span class="inner icon-remove_circle_outline"></span>
      </div>
    </transition>
    <div class="cart-count" v-show="food.count>0">{{food.count}}</div>
    <div class="cart-add icon-add_circle" @click="addCart"></div>
  </div>
</template>

<script>
  import Vue from 'vue';

  export default {
    name: 'cartcontrol',
    props: {
      food: {
        type: Object
      }
    },
    methods: {
      addCart() {
        if (!this.food.count) {
          // this.food.count = 1;必须用Vue.set设置新属性，否则Vue检测不到
          Vue.set(this.food, 'count', 1);
        } else {
          this.food.count++;
        }

        this.$emit('add', event.target);
      },
      decreaseCart() {
        if (this.food.count) {
          this.food.count--;
        }
      }
    }
  };
</script>

<style lang="stylus" ref="StyleSheet/stylus">
  .cartcontrol
    // inline-block后元素之间会有间隙，设置font-size
    font-size: 0
    .cart-decrease
      display: inline-block
      // 通过padding增加可点击区域
      padding: 6px
      opacity: 1
      transform: translate3d(0, 0, 0)
      .inner
        display: inline-block
        line-height: 24px
        font-size: 24px
        color: rgb(0, 160, 220)
        transition: all 0.4s linear
        transform: rotate(0)
      &.move-enter-active, &.move-leave-active
        transition: all 0.4s linear
      &.move-enter, &.move-leave-to
        opacity: 0
        transform: translate3d(24px, 0, 0)
        .inner
          transform: rotate(180deg)
    .cart-count
      display: inline-block
      vertical-align: top
      width: 12px
      padding-top: 6px
      line-height: 24px
      text-align: center
      font-size: 10px
      color: rgb(147, 153, 159)
    .cart-add
      display: inline-block
      // 通过padding增加可点击区域
      padding: 6px
      font-size: 24px
      line-height: 24px
      color: rgb(0, 160, 220)
</style>
