package mass.adhoc.s4yd

import java.time.OffsetDateTime

class RvCategory {
  var id: Int = 0
  var name: String = _
  var parentId: Int = _
  var status: Int = _
}

case class ArticleVolume(
    var id: java.lang.Long,
    var sortname: String,
    var name: String,
    var articleId: java.lang.Long,
    var serialNo: Integer,
    var description: String,
    var createTime: OffsetDateTime) {
  def this() {
    this(null, null, null, null, null, null, null)
  }
}

class RvBook {
  var author: Long = 0L
  var id: Long = 0L
  var name: String = _
  var keywords: String = _
  var intro: String = _
  var progress: Int = _
  var subcategory: Long = _
  var isvip: Int = _
  var sex: Int = 0
}

case class ArtPart(
    id: Long,
    name: String,
    bookId: Long,
    context: String,
    sort: Int,
    createTime: OffsetDateTime,
    createBy: Long = 1
)

class RvArticle {
  var author: Long = 0L
  var id: Long = 0L
  var name: String = _
  var articleId: Long = 0L
  var volumeId: Long = 0L
  var size: Long = 0L
  var order: Int = 0
  var extra: String = _
  var status: Int = 0
  var isvip: Int = 0
  var audited: Int = 0
  var publishType: Int = 0
  var publishTime: OffsetDateTime = _
  var createTime: OffsetDateTime = _
  var updateTime: OffsetDateTime = _
  var platformCid: Integer = _
  var content: String = _
  var cover: String = _

  override def toString =
    s"ArticleChapter($id, $name, $articleId, $volumeId, $size, $order, $extra, $status, $isvip, $audited, $publishType, $createTime, $updateTime, $platformCid, $content)"
}

case class ArtArticle(
    id: Long,
    createBy: Long,
    createTime: OffsetDateTime,
    title: String,
    content: String,
    bookId: Long,
    partId: Long,
    wordsSize: Long,
    checkStatus: String,
    showId: String,
    sort: Int,
    needVip: String,
    publishTime: OffsetDateTime,
    draftStatus: String = "1"
)

class RvUser {
  var id: Long = _
  var username: String = _
  var email: String = _
  var mobile: String = _
  var status: Integer = _
  var level: Integer = _
  var vip: Integer = _
  var egoId: Integer = _
  var regtime: OffsetDateTime = _
  var nickname: String = _
  var realname: String = _
  var penname: String = _
  var gender: Int = 0
  var avatar: String = _
  var idcardtype: Integer = _
  var idcard: String = _
  var qq: String = _
  var birthday: String = _
  var constellation: String = _
  var zodiac: String = _
  var profileMobile: String = _
  var province: String = _
  var city: String = _
  var address: String = _
}

case class TdUser(
    createTime: OffsetDateTime,
    createBy: Long,
    status: Int,
    account: String,
    vip: String,
    nickName: String,
    email: String,
    phone: String,
    coverId: Long,
    id: Long
)

case class TdUserAuthor(
    userId: Long,
    id: Long,
    name: String,
    penName: String,
    createBy: Long,
    createTime: OffsetDateTime,
    intro: String,
    worksNum: Int,
    wordsSize: Long,
    workDayNum: Int,
    checkStatus: String
)

case class RvBaseFile(
    originName: String,
    suffixes: String,
    createBy: Long,
    createTime: OffsetDateTime,
    status: String,
    path: String
)
