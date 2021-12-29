package ru.avalon.javapp.devj130;

public class DbServer implements IDbService {
    @Override
    public boolean addAuthor(Author author) throws DocumentException {
        return false;
    }

    @Override
    public boolean addDocument(Document doc, Author author) throws DocumentException {
        return false;
    }

    @Override
    public Document[] findDocumentByAuthor(Author author) throws DocumentException {
        return new Document[0];
    }

    @Override
    public Document[] findDocumentByContent(String content) throws DocumentException {
        return new Document[0];
    }

    @Override
    public boolean deleteAuthor(Author author) throws DocumentException {
        return false;
    }

    @Override
    public boolean deleteAuthor(int id) throws DocumentException {
        return false;
    }

    @Override
    public void close() throws Exception {

    }
}
