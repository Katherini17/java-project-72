@import hexlet.code.dto.BasePage
@param BasePage page

@template.layout.page(
    page = page,
    content = @`
        <div class="container-fluid h-100 w-100 d-flex flex-column align-items-center justify-content-center">
            <section class="p-3 w-100">
                <form action="/urls" method="post">
                    <div class="mb-3">
                        <label class="form-label mb-1" for="url">URL</label>
                        <input class="form-control" type="text" name="url" id="url" placeholder="https://www.example.com" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Добавить</button>
                </form>
            </section>
        </div>
`)