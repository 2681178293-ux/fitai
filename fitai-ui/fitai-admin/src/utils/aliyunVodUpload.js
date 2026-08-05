/**
 * 阿里云视频点播上传工具
 * 基于 ali-oss SDK 实现
 */
import OSS from "ali-oss";

/**
 * 解析上传地址
 * @param {string} uploadAddress Base64 编码的上传地址
 * @returns {Object} 解析后的上传地址信息
 */
const parseUploadAddress = uploadAddress => {
  try {
    const decoded = atob(uploadAddress);
    return JSON.parse(decoded);
  } catch (e) {
    console.error("解析上传地址失败:", e);
    throw new Error("无效的上传地址");
  }
};

/**
 * 解析上传凭证
 * @param {string} uploadAuth Base64 编码的上传凭证
 * @returns {Object} 解析后的凭证信息
 */
const parseUploadAuth = uploadAuth => {
  try {
    const decoded = atob(uploadAuth);
    return JSON.parse(decoded);
  } catch (e) {
    console.error("解析上传凭证失败:", e);
    throw new Error("无效的上传凭证");
  }
};

/**
 * 创建 OSS 客户端
 * @param {Object} addressInfo 上传地址信息
 * @param {Object} authInfo 凭证信息
 * @returns {OSS} OSS 客户端实例
 */
const createOssClient = (addressInfo, authInfo) => {
  return new OSS({
    region: addressInfo.Endpoint.replace("https://", "").split(".")[0],
    accessKeyId: authInfo.AccessKeyId,
    accessKeySecret: authInfo.AccessKeySecret,
    stsToken: authInfo.SecurityToken,
    bucket: addressInfo.Bucket,
    secure: true
  });
};

/**
 * 上传视频到阿里云 VOD
 * @param {Object} credential 上传凭证 { uploadAddress, uploadAuth, vodVideoId }
 * @param {File} file 要上传的文件
 * @param {Function} onProgress 进度回调 (percent: number) => void
 * @returns {Promise<void>}
 */
export const uploadToAliyunVod = async (credential, file, onProgress) => {
  const { uploadAddress, uploadAuth } = credential;

  // 解析凭证
  const addressInfo = parseUploadAddress(uploadAddress);
  const authInfo = parseUploadAuth(uploadAuth);

  console.log("阿里云 VOD 上传信息:", {
    bucket: addressInfo.Bucket,
    endpoint: addressInfo.Endpoint,
    fileName: addressInfo.FileName
  });

  // 创建 OSS 客户端
  const ossClient = createOssClient(addressInfo, authInfo);

  // 分片上传（支持大文件和断点续传）
  const result = await ossClient.multipartUpload(addressInfo.FileName, file, {
    parallel: 4, // 并发分片数
    partSize: 1024 * 1024 * 2, // 分片大小 2MB
    progress: (p, checkpoint) => {
      const percent = Math.round(p * 100);
      console.log(`上传进度: ${percent}%`);
      if (onProgress) {
        onProgress(percent);
      }
    },
    headers: {
      "Content-Type": file.type || "video/mp4"
    }
  });

  console.log("阿里云 VOD 上传完成:", result);
  return result;
};

/**
 * 简单上传（小文件）
 * @param {Object} credential 上传凭证
 * @param {File} file 要上传的文件
 * @param {Function} onProgress 进度回调
 * @returns {Promise<void>}
 */
export const simpleUploadToAliyunVod = async (credential, file, onProgress) => {
  const { uploadAddress, uploadAuth } = credential;

  // 解析凭证
  const addressInfo = parseUploadAddress(uploadAddress);
  const authInfo = parseUploadAuth(uploadAuth);

  // 创建 OSS 客户端
  const ossClient = createOssClient(addressInfo, authInfo);

  // 普通上传
  const result = await ossClient.put(addressInfo.FileName, file, {
    headers: {
      "Content-Type": file.type || "video/mp4"
    }
  });

  if (onProgress) {
    onProgress(100);
  }

  return result;
};

/**
 * 判断是否为阿里云 VOD 凭证
 * @param {Object} credential 凭证对象
 * @returns {boolean}
 */
export const isAliyunVodCredential = credential => {
  return !!(credential.uploadAddress && credential.uploadAuth);
};

/**
 * 判断是否为 MinIO 凭证
 * @param {Object} credential 凭证对象
 * @returns {boolean}
 */
export const isMinioCredential = credential => {
  return !!credential.uploadUrl;
};

export default {
  uploadToAliyunVod,
  simpleUploadToAliyunVod,
  isAliyunVodCredential,
  isMinioCredential
};
