<template>
  <div class="slider" ref="slider">
    <div class="slider-group" ref="sliderGroup">
      <slot></slot>
    </div>
    <div class="dots">
      <span class="dot" :class="{active: currentPageIndex === index }" v-for="(item, index) in dots"></span>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
  import BScroll from 'better-scroll';
  import {addClass} from '../../common/js/dom';

  export default {
    props: {
      loop: {
        type: Boolean,
        default: true
      },
      autoPlay: {
        type: Boolean,
        default: true
      },
      interval: {
        type: Number,
        default: 4000
      },
      showDot: {
        type: Boolean,
        default: true
      },
      click: {
        type: Boolean,
        default: true
      },
      threshold: {
        type: Number,
        default: 0.3
      },
      speed: {
        type: Number,
        default: 400
      }
    },
    data() {
      return {
        dots: [],
        currentPageIndex: 0
      };
    },
    mounted() {
      setTimeout(() => {
        this._setSliderWidth();
        this._initDots();
        this._initSlider();
        if (this.autoPlay) {
          this._play();
        }
      }, 20);

      window.addEventListener('resize', () => {
        if (!this.slider) {
          return;
        }
        this._setSliderWidth(true);
        this.slider.refresh();
      });
    },
    methods: {
      _setSliderWidth(isResize) {
        this.children = this.$refs.sliderGroup.children;

        let width = 0;
        let sliderWidth = this.$refs.slider.clientWidth;
        for (let i = 0; i < this.children.length; i++) {
          let child = this.children[i];
          addClass(child, 'slider-item');

          child.style.width = sliderWidth + 'px';
          // 总宽度
          width += sliderWidth;
        }

        // 轮播,参考swiper至少需要克隆首尾的2个dom(3->1,2,3->1)
        // 所以总宽度需要额外增加2个sliderWidth宽度;如果是isresize过来的就不需要加了
        // 说白了就是异步加载数据导致dom未被正确填充,无法正确计算content的width
        // 已知wrapper容器clientWidth固定的情况下
        // 需要人工指定content(父容器的第一个子元素)的style.width样式属性值供BScroll获取
        if (this.loop && !isResize) {
          width += 2 * sliderWidth;
        }
        this.$refs.sliderGroup.style.width = width + 'px';
      },
      _initDots() {
        this.dots = new Array(this.children.length);
      },
      _initSlider() {
        this.slider = new BScroll(this.$refs.slider, {
          scrollX: true,
          scrollY: false,
          momentum: false,
          snap: {
            loop: this.loop,
            threshold: this.threshold,
            speed: this.speed
          },
          bounce: false,
          click: this.click
        });

        this.slider.on('scrollEnd', () => {
          let pageIndex = this.slider.getCurrentPage().pageX;
          this.currentPageIndex = pageIndex;

          if (this.autoPlay) {
            clearTimeout(this.timer);
            this._play();
          }
        });
      },
      _play() {
        let pageIndex = this.currentPageIndex + 1;
        this.timer = setTimeout(() => {
          this.slider.goToPage(pageIndex, 0, this.speed);
        }, this.interval);
      }
    },
    destroyed() {
      clearTimeout(this.timer);
    }
  };
</script>

<style scoped lang="stylus" rel="stylesheet/stylus">
  @import "~common/stylus/variable"

  .slider
    min-height: 1px
    .slider-group
      position: relative
      overflow: hidden
      white-space: nowrap
      .slider-item
        float: left
        box-sizing: border-box
        overflow: hidden
        text-align: center
        a
          display: block
          width: 100%
          overflow: hidden
          text-decoration: none
        img
          display: block
          width: 100%
    .dots
      position: absolute
      right: 0
      left: 0
      bottom: 12px
      text-align: center
      font-size: 0
      .dot
        display: inline-block
        margin: 0 4px
        width: 8px
        height: 8px
        border-radius: 50%
        background: $color-text-l
        &.active
          width: 20px
          border-radius: 5px
          background: $color-text-ll
</style>
