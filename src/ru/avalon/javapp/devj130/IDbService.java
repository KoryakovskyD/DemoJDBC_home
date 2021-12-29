package ru.avalon.javapp.devj130;

/**
 * Интерфейс определяет методы доступа к базе данных документов.
 *
 * @author (C)Y.D.Zakovryashin, 02.11.2020
 */
public interface IDbService extends AutoCloseable {
    /**
     * Метод добавляет нового автора к базе данных, если все обязательные поля
     * объекта author определены. В противном случае, метод пытается обновить
     * уже существующие записи, используя заполненные поля класса для поиска
     * подходящих записей. Например, если в объекте author указан id автора,
     * поле имени автора пусто, а поле примечаний содержит текст, то у записи с
     * заданным идентификатором обновляется поле примечаний.
     *
     * @param author именные данные автора.
     * @return возвращает значение true, если создана новая запись, и значение
     * false, если обновлена существующая запись.
     * @throws DocumentException выбрасывается в случае, если поля объекта
     * author заполнены неправильно и не удаётся создать новую запись или
     * обновить уже существующую. Данное исключение также выбрасывается в случае
     * общей ошибки доступа к базе данных
     */
    boolean addAuthor(Author author) throws DocumentException;
    /**
     * Метод добавляет новый документ к базе данных, если все обязательные поля
     * объектов doc и author определены. В противном случае, метод пытается
     * обновить уже существующие записи, используя заполненные поля объектов для
     * поиска подходящих записей.
     *
     * @param doc добавляемый или обновляемый документ.
     * @param author ссылка на автора документа.
     * @return возвращает значение true, если создан новый документ, и значение
     * false, если обновлена уже существующая запись.
     * @throws DocumentException выбрасывается в случае, если поля объектов doc
     * и author заполнены неправильно и не удаётся создать новую запись или
     * обновить уже существующую. Данное исключение также выбрасывается в случае
     * общей ошибки доступа к базе данных
     */
    boolean addDocument(Document doc, Author author) throws DocumentException;
    /**
     * Метод производит поиск документов по их автору.
     *
     * @param author автор документа. Объект может содержать неполную информацию
     * об авторе. Например, объект может содержать только именные данные автора
     * или только его идентификатор.
     * @return возвращает массив всех найденных документов. Если в базе данных
     * не найдено ни одного документа, то возвращается значение null.
     * @throws DocumentException выбрасывается в случае, если поле объекта
     * author заполнены неправильно или нелья выполнить поиск по его полям.
     * Данное исключение также выбрасывается в случае общей ошибки доступа к
     * базе данных
     */
    Document[] findDocumentByAuthor(Author author) throws DocumentException;
    /**
     * Метод производит поиск документов по их содержанию.
     *
     * @param content фрагмент текста (ключевые слова), который должен
     * содержаться в заголовке или в основном тексте документа.
     * @return возвращает массив найденных документов.Если в базе данных не
     * найдено ни одного документа, удовлетворяющего условиям поиска, то
     * возвращается значение null.
     * @throws DocumentException выбрасывается в случае, если строка content
     * равна null или является пустой. Данное исключение также выбрасывается в
     * случае общей ошибки доступа к базе данных
     */
    Document[] findDocumentByContent(String content) throws DocumentException;
    /**
     * Метод удаляет автора из базы данных. Всесте с автором удаляются и все
     * документы, которые ссылаются на удаляемого автора.
     *
     * @param author удаляемый автор. Объект может содержать неполные данные
     * автора, например, только идентификатор автора.
     * @return значение true, если запись автора успешно удалена, и значение
     * false - в противном случае.
     * @throws DocumentException выбрасывается в случае, если поля объекта
     * author заполнены неправильно или ссылка author равна null, а также случае
     * общей ошибки доступа к базе данных.
     */
    boolean deleteAuthor(Author author) throws DocumentException;
    /**
     * Метод удаляет автора из базы данных по его идентификатору. Всесте с
     * автором удаляются и все документы, которые ссылаются на удаляемого
     * автора.
     *
     * @param id идентификатор удаляемого автора.
     * @return значение true, если запись автора успешно удалена, и значение
     * false - в противном случае.
     * @throws DocumentException выбрасывается в случае общей ошибки доступа к
     * базе данных.
     */
    boolean deleteAuthor(int id) throws DocumentException;
}