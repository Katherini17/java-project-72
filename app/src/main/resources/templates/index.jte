@import hexlet.code.dto.BasePage
@param BasePage page

@template.layout.page(
    page = page,
    content = @`
        <section class="w-100">
            <form action="/urls" method="post">
                <div class="mb-3">
                    <label class="form-label mb-1" for="url">URL</label>
                    <input class="form-control" type="text" name="url" id="url" placeholder="https://www.example.com" required>
                </div>
                <button type="submit" class="btn btn-primary">Добавить</button>
            </form>
        </section>
`)