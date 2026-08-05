const fs = require('fs')
const path = require('path')
const sharp = require('sharp')

const w = 750
const h = 360
const dir = path.join(__dirname, '../static')
const dirSrc = path.join(__dirname, '../src/static')
if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true })
if (!fs.existsSync(dirSrc)) fs.mkdirSync(dirSrc, { recursive: true })

const svg = `
  <svg xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}" viewBox="0 0 ${w} ${h}">
    <defs>
      <linearGradient id="grad" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" style="stop-color:#3b82f6"/>
        <stop offset="100%" style="stop-color:#2563eb"/>
      </linearGradient>
    </defs>
    <rect width="100%" height="100%" fill="url(#grad)"/>
    <text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" font-family="Arial,sans-serif" font-size="48" fill="rgba(255,255,255,0.9)">FitAI 智能健身</text>
  </svg>
`

;(async () => {
  await sharp(Buffer.from(svg)).png().toFile(path.join(dir, 'banner-default.png'))
  fs.copyFileSync(path.join(dir, 'banner-default.png'), path.join(dirSrc, 'banner-default.png'))
  console.log('Banner default image generated')
})().catch(e => { console.error(e); process.exit(1) })
