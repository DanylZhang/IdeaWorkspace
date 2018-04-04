<template>
  <transition name="slide">
    <music-list :title="title" :bgImage="bgImage" :songs="songs"></music-list>
  </transition>
</template>

<script type="text/ecmascript-6">
  import MusicList from '../music-list/music-list'
  import {mapGetters} from 'vuex'
  import {getDiscSongList} from '../../api/recommend'
  import {ERR_OK} from '../../api/config'
  import {createSong} from '../../common/js/song'

  export default {
    name: 'disc',
    data() {
      return {
        songs: []
      }
    },
    computed: {
      title() {
        return this.disc.title
      },
      bgImage() {
        return this.disc.cover
      },
      ...mapGetters([
        'disc'
      ])
    },
    components: {
      MusicList
    },
    created() {
      this._getSongList()
    },
    methods: {
      _getSongList() {
        if (!this.disc.content_id) {
          this.$router.push('/recommend')
          return
        }
        getDiscSongList(this.disc.content_id).then((res) => {
          if (res.code === ERR_OK) {
            let list = res.cdlist[0].songlist
            this.songs = this._normalizeSongs(list)
          }
        })
      },
      _normalizeSongs(list) {
        let ret = []
        list.forEach((musicData) => {
          if (musicData.songid && musicData.albumid) {
            createSong(musicData).then((res) => {
              ret.push(res)
            })
          }
        })
        return ret
      }
    }
  }
</script>

<style scoped lang="stylus" rel="stylesheet/stylus">
  .slide-enter-active, .slide-leave-active
    transition: all 0.3s

  .slide-enter, .slide-leave-to
    transform: translate3d(100%, 0, 0)
</style>
