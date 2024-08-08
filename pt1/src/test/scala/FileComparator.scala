object FileComparator:
  def compareFiles(path1: String, path2: String): Boolean =
    val file1 = scala.io.Source.fromFile(path1)
    val file2 = scala.io.Source.fromFile(path2)
    val lines1 = file1.getLines().toList.map(_.split(":")(1).toDouble)
    val lines2 = file2.getLines().toList.map(_.split(":")(1).toDouble).take(lines1.size)
    file1.close()
    file2.close()
    var eq = true
    for (n1, n2) <- lines1.zip(lines2) do if Math.abs(n1 - n2) > 0.0001 then eq = false
    eq
