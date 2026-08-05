const fs = require('fs')
const path = require('path')
const sharp = require('sharp')

const size = 81
const dir = path.join(__dirname, '../static/tabbar')
const dirSrc = path.join(__dirname, '../src/static/tabbar')
if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true })
if (!fs.existsSync(dirSrc)) fs.mkdirSync(dirSrc, { recursive: true })

const gray = '#999999'
const blue = '#3b82f6'

const icons = [
  {
    name: 'home',
    svg: (c) => `
      <svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="${c}" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
        <polyline points="9 22 9 12 15 12 15 22"/>
      </svg>
    `
  },
  {
    name: 'course',
    svg: (c) => `
      <svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="${c}" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
        <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
        <line x1="8" y1="7" x2="16" y2="7"/>
        <line x1="8" y1="11" x2="16" y2="11"/>
      </svg>
    `
  },
  {
    name: 'ai',
    svg: (c) => `
      <svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="${c}" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
      </svg>
    `
  },
  {
    name: 'shop',
    svg: (c) => `
      <svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="${c}" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/>
        <line x1="3" y1="6" x2="21" y2="6"/>
        <path d="M16 10a4 4 0 0 1-8 0"/>
      </svg>
    `
  },
  {
    name: 'my',
    svg: (c) => `
      <svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="${c}" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
        <circle cx="12" cy="7" r="4"/>
      </svg>
    `
  }
]

async function generate() {
  for (const { name, svg } of icons) {
    const graySvg = Buffer.from(svg(gray))
    const blueSvg = Buffer.from(svg(blue))
    const grayPath = path.join(dir, `${name}.png`)
    const bluePath = path.join(dir, `${name}-active.png`)
    await sharp(graySvg).png().toFile(grayPath)
    await sharp(blueSvg).png().toFile(bluePath)
    fs.copyFileSync(grayPath, path.join(dirSrc, `${name}.png`))
    fs.copyFileSync(bluePath, path.join(dirSrc, `${name}-active.png`))
  }
  console.log('Tabbar icons generated in static/tabbar/ and src/static/tabbar/')
}

generate().catch(err => {
  console.error(err)
  process.exit(1)
})
