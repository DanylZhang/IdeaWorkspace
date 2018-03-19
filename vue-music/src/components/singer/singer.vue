<template>
  <div class="singer">歌手</div>
</template>

<script>
  import {getSingerList} from '../../api/singer';
  import {ERR_OK} from '../../api/config';

  export default {
    name: 'singer',
    data() {
      return {
        singers: []
      };
    },
    created() {
      this._getSingerList();
    },
    methods: {
      _getSingerList() {
        getSingerList().then((res) => {
          if (res.code === ERR_OK) {
            this.singers = res.data.list;
          }
        });
      }
    }
  };
</script>

<style lang="stylus">
  .singer
    position: fixed
    top: 88px
    bottom: 0
    widows: 100%
</style>
