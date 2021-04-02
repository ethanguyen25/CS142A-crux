package crux;

final class Authors {
  // TODO: Add author information.
  static final Author[] all = {new Author("Ethan Nguyen", "16301330", "ethandn1"),
          new Author("Arian Namavar", "65391551", "anamava1")};
}


final class Author {
  final String name;
  final String studentId;
  final String uciNetId;

  Author(String name, String studentId, String uciNetId) {
    this.name = name;
    this.studentId = studentId;
    this.uciNetId = uciNetId;
  }
}
