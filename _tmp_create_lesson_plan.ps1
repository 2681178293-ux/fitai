$desktop = [Environment]::GetFolderPath('Desktop')
$outPath = Join-Path $desktop '教案模板.docx'
$tempDir = Join-Path $env:TEMP ("lesson_plan_" + [guid]::NewGuid().ToString())
New-Item -ItemType Directory -Path $tempDir -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $tempDir '_rels') -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $tempDir 'word\_rels') -Force | Out-Null

@'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
  <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
</Types>
'@ | Out-File -LiteralPath (Join-Path $tempDir '[Content_Types].xml') -Encoding utf8

@'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>
'@ | Out-File -LiteralPath (Join-Path $tempDir '_rels\.rels') -Encoding utf8

@'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>
'@ | Out-File -LiteralPath (Join-Path $tempDir 'word\_rels\document.xml.rels') -Encoding utf8

@'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:docDefaults>
    <w:rPrDefault>
      <w:rPr>
        <w:rFonts w:ascii="宋体" w:eastAsia="宋体" w:hAnsi="宋体"/>
        <w:sz w:val="24"/>
        <w:szCs w:val="24"/>
      </w:rPr>
    </w:rPrDefault>
  </w:docDefaults>
  <w:style w:type="paragraph" w:styleId="Normal" w:default="1">
    <w:name w:val="Normal"/>
    <w:qFormat/>
  </w:style>
</w:styles>
'@ | Out-File -LiteralPath (Join-Path $tempDir 'word\styles.xml') -Encoding utf8

$documentXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
            xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing"
            xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <w:body>
    <w:p>
      <w:pPr><w:jc w:val="left"/></w:pPr>
      <w:r><w:t xml:space="preserve">授课时间：</w:t></w:r>
      <w:r><w:t xml:space="preserve">      </w:t></w:r>
      <w:r><w:t>年</w:t></w:r>
      <w:r><w:t xml:space="preserve">      </w:t></w:r>
      <w:r><w:t>月</w:t></w:r>
      <w:r><w:t xml:space="preserve">      </w:t></w:r>
      <w:r><w:t>日</w:t></w:r>
      <w:r><w:t xml:space="preserve">  星期      课型              本学期第      课时</w:t></w:r>
    </w:p>

    <w:tbl>
      <w:tblPr>
        <w:tblW w:w="5000" w:type="pct"/>
        <w:tblBorders>
          <w:top w:val="single" w:sz="4" w:space="0" w:color="000000"/>
          <w:left w:val="single" w:sz="4" w:space="0" w:color="000000"/>
          <w:bottom w:val="single" w:sz="4" w:space="0" w:color="000000"/>
          <w:right w:val="single" w:sz="4" w:space="0" w:color="000000"/>
          <w:insideH w:val="single" w:sz="4" w:space="0" w:color="000000"/>
          <w:insideV w:val="single" w:sz="4" w:space="0" w:color="000000"/>
        </w:tblBorders>
        <w:tblLook w:val="04A0" w:firstRow="1" w:lastRow="0" w:firstColumn="1" w:lastColumn="0" w:noHBand="0" w:noVBand="1"/>
      </w:tblPr>
      <w:tblGrid>
        <w:gridCol w:w="900"/>
        <w:gridCol w:w="1400"/>
        <w:gridCol w:w="5200"/>
        <w:gridCol w:w="900"/>
      </w:tblGrid>

      <!-- 教学内容 -->
      <w:tr>
        <w:tc>
          <w:tcPr><w:vAlign w:val="center"/><w:tcW w:w="900" w:type="dxa"/></w:tcPr>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>教学内容</w:t></w:r></w:p>
        </w:tc>
        <w:tc>
          <w:tcPr><w:gridSpan w:val="2"/><w:tcW w:w="6600" w:type="dxa"/><w:vAlign w:val="top"/></w:tcPr>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
        </w:tc>
        <w:tc>
          <w:tcPr><w:vAlign w:val="center"/><w:tcW w:w="900" w:type="dxa"/><w:vMerge w:val="restart"/></w:tcPr>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>（二次</w:t></w:r></w:p>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>备课）</w:t></w:r></w:p>
        </w:tc>
      </w:tr>

      <!-- 教学目标 - 知识与技能 -->
      <w:tr>
        <w:tc>
          <w:tcPr><w:vAlign w:val="center"/><w:tcW w:w="900" w:type="dxa"/><w:vMerge w:val="restart"/></w:tcPr>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>教</w:t></w:r></w:p>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>学</w:t></w:r></w:p>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>目</w:t></w:r></w:p>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>标</w:t></w:r></w:p>
        </w:tc>
        <w:tc>
          <w:tcPr><w:vAlign w:val="center"/><w:tcW w:w="1400" w:type="dxa"/></w:tcPr>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>知识与技能</w:t></w:r></w:p>
        </w:tc>
        <w:tc>
          <w:tcPr><w:tcW w:w="5200" w:type="dxa"/><w:vAlign w:val="top"/></w:tcPr>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
        </w:tc>
        <w:tc>
          <w:tcPr><w:vMerge w:val="continue"/></w:tcPr>
          <w:p><w:r><w:t></w:t></w:r></w:p>
        </w:tc>
      </w:tr>

      <!-- 过程与方法 -->
      <w:tr>
        <w:tc><w:tcPr><w:vMerge w:val="continue"/></w:tcPr><w:p><w:r><w:t></w:t></w:r></w:p></w:tc>
        <w:tc>
          <w:tcPr><w:vAlign w:val="center"/><w:tcW w:w="1400" w:type="dxa"/></w:tcPr>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>过程与方法</w:t></w:r></w:p>
        </w:tc>
        <w:tc>
          <w:tcPr><w:tcW w:w="5200" w:type="dxa"/><w:vAlign w:val="top"/></w:tcPr>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
        </w:tc>
        <w:tc><w:tcPr><w:vMerge w:val="continue"/></w:tcPr><w:p><w:r><w:t></w:t></w:r></w:p></w:tc>
      </w:tr>

      <!-- 情感态度价值观 -->
      <w:tr>
        <w:tc><w:tcPr><w:vMerge w:val="continue"/></w:tcPr><w:p><w:r><w:t></w:t></w:r></w:p></w:tc>
        <w:tc>
          <w:tcPr><w:vAlign w:val="center"/><w:tcW w:w="1400" w:type="dxa"/></w:tcPr>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>情感、态度、价值观</w:t></w:r></w:p>
        </w:tc>
        <w:tc>
          <w:tcPr><w:tcW w:w="5200" w:type="dxa"/><w:vAlign w:val="top"/></w:tcPr>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
        </w:tc>
        <w:tc><w:tcPr><w:vMerge w:val="continue"/></w:tcPr><w:p><w:r><w:t></w:t></w:r></w:p></w:tc>
      </w:tr>

      <!-- 重点 -->
      <w:tr>
        <w:tc>
          <w:tcPr><w:vAlign w:val="center"/><w:tcW w:w="900" w:type="dxa"/><w:vMerge w:val="restart"/></w:tcPr>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>教学</w:t></w:r></w:p>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>重、难点</w:t></w:r></w:p>
        </w:tc>
        <w:tc>
          <w:tcPr><w:vAlign w:val="center"/><w:tcW w:w="1400" w:type="dxa"/></w:tcPr>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>重点</w:t></w:r></w:p>
        </w:tc>
        <w:tc>
          <w:tcPr><w:tcW w:w="5200" w:type="dxa"/><w:vAlign w:val="top"/></w:tcPr>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
        </w:tc>
        <w:tc><w:tcPr><w:vMerge w:val="continue"/></w:tcPr><w:p><w:r><w:t></w:t></w:r></w:p></w:tc>
      </w:tr>

      <!-- 难点 -->
      <w:tr>
        <w:tc><w:tcPr><w:vMerge w:val="continue"/></w:tcPr><w:p><w:r><w:t></w:t></w:r></w:p></w:tc>
        <w:tc>
          <w:tcPr><w:vAlign w:val="center"/><w:tcW w:w="1400" w:type="dxa"/></w:tcPr>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>难点</w:t></w:r></w:p>
        </w:tc>
        <w:tc>
          <w:tcPr><w:tcW w:w="5200" w:type="dxa"/><w:vAlign w:val="top"/></w:tcPr>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
        </w:tc>
        <w:tc><w:tcPr><w:vMerge w:val="continue"/></w:tcPr><w:p><w:r><w:t></w:t></w:r></w:p></w:tc>
      </w:tr>

      <!-- 师生活动 标题行 -->
      <w:tr>
        <w:tc>
          <w:tcPr><w:gridSpan w:val="3"/><w:tcW w:w="7500" w:type="dxa"/><w:vAlign w:val="center"/></w:tcPr>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>师  生  活  动</w:t></w:r></w:p>
        </w:tc>
        <w:tc>
          <w:tcPr><w:vAlign w:val="center"/><w:tcW w:w="900" w:type="dxa"/></w:tcPr>
          <w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>（二次备课）</w:t></w:r></w:p>
        </w:tc>
      </w:tr>

      <!-- 师生活动 内容行 -->
      <w:tr>
        <w:tc>
          <w:tcPr><w:gridSpan w:val="3"/><w:tcW w:w="7500" w:type="dxa"/><w:vAlign w:val="top"/></w:tcPr>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
        </w:tc>
        <w:tc>
          <w:tcPr><w:vAlign w:val="top"/><w:tcW w:w="900" w:type="dxa"/></w:tcPr>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
          <w:p><w:r><w:t xml:space="preserve"> </w:t></w:r></w:p>
        </w:tc>
      </w:tr>
    </w:tbl>

    <w:sectPr>
      <w:pgSz w:w="11906" w:h="16838"/>
      <w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440" w:header="708" w:footer="708" w:gutter="0"/>
    </w:sectPr>
  </w:body>
</w:document>
'@

$documentXml | Out-File -LiteralPath (Join-Path $tempDir 'word\document.xml') -Encoding utf8

if (Test-Path $outPath) { Remove-Item $outPath -Force }
Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::CreateFromDirectory($tempDir, $outPath)
Remove-Item $tempDir -Recurse -Force

Write-Output "CREATED: $outPath"
