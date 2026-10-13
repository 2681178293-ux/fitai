package net.wanho.fitai.modules.media.code;

import net.wanho.fitai.infra.exception.ExceptionResult;

/**
 * 系统模块错误码 2001-2999
 */
public enum MediaCode implements ExceptionResult {
    MEDIA_CATEGORY_NOT_FOUND(7005, "分类不存在"),
    MEDIA_CATEGORY_HAS_IMAGES(7006, "请先清空该分类下的图片"),
    MEDIA_CATEGORY_HAS_CHILDREN(7007, "请先删除子分类"),
    MEDIA_OSS_NOT_FOUND(7008, "图片不存在"),
    MEDIA_CATEGORY_HAS_VIDEOS(7010, "请先清空该分类下的视频"),
    MEDIA_VOD_NOT_FOUND(7011, "视频不存在"),
    VOD_PLAY_URL_NOT_READY(7013, "视频暂无播放地址"),
    MEDIA_CATEGORY_EXISTS(7005, "分类不存在");

    private final int code;
    private final String msg;

    MediaCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public String msg() {
        return this.msg;
    }
}
