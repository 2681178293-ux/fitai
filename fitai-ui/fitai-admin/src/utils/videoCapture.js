/**
 * 从视频文件截取第一帧作为封面
 * @param {File} file 视频文件
 * @param {number} seekTime 截取时间点（秒），默认 1
 * @returns {Promise<{ coverBlob: Blob, duration: number }>}
 */
export function captureVideoFrame(file, seekTime = 1) {
  return new Promise((resolve, reject) => {
    const video = document.createElement("video");
    video.muted = true;
    video.playsInline = true;
    video.crossOrigin = "anonymous";

    const url = URL.createObjectURL(file);

    video.onloadedmetadata = () => {
      const duration = Math.floor(video.duration) || 0;
      const time = Math.min(seekTime, Math.max(0, duration - 0.1));

      video.onseeked = () => {
        try {
          const canvas = document.createElement("canvas");
          canvas.width = video.videoWidth;
          canvas.height = video.videoHeight;
          const ctx = canvas.getContext("2d");
          ctx.drawImage(video, 0, 0);
          canvas.toBlob(
            blob => {
              URL.revokeObjectURL(url);
              video.src = "";
              if (blob) {
                resolve({ coverBlob: blob, duration });
              } else {
                reject(new Error("截帧失败"));
              }
            },
            "image/jpeg",
            0.85
          );
        } catch (e) {
          URL.revokeObjectURL(url);
          reject(e);
        }
      };

      video.onerror = () => {
        URL.revokeObjectURL(url);
        reject(new Error("视频加载失败"));
      };

      video.currentTime = time;
    };

    video.onerror = () => {
      URL.revokeObjectURL(url);
      reject(new Error("视频加载失败"));
    };

    video.src = url;
  });
}
