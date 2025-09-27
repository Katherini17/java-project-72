@template.layout.page(
    page = page,
    content = @`
        <div class="container-fluid bg-light">
            <section class="p-3">
                <form action="/urls" method="post">
                    <div class="mb-3">
                        <label class="form-label mb-1" for="url">URL</label>
                        <input class="form-control" type="url" name="url" id="url" placeholder="https://www.example.com">
                    </div>
                    <button type="submit" class="btn btn-primary">Добавить</button>
                </form>
            </section>
        </div>
`)