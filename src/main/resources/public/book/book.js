function deleteBook() {
    const deleteIdElements = document.getElementsByName('book_id_list[]');
    const deleteCheckValue = Array.prototype.slice.call(deleteIdElements)
                                                .filter(element => element.checked)
                                                .map(element => element.value)
                                                .join(',');
    if (deleteCheckValue == '') {
        alert('削除対象の書籍を選択してください。');
        return false;
    }
    document.getElementById('book_id_list').value = deleteCheckValue;
    document.getElementById('delete-form').submit();
}
