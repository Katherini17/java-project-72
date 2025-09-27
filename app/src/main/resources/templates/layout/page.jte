@import hexlet.code.dto.BasePage
@import gg.jte.Content

@param Content content
@param BasePage page = null

<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>${page != null && page.getTitle() != null ? page.getTitle() : "Анализатор страниц"}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
</head>
<body>
    <header>

    </header>
    <main>
        @if(page != null && page.getFlash() != null)
            @if(page.getFlashType() == "success")
            <div class="alert alert-success" role="alert">
                ${page.getFlash()}
            </div>
            @elseif(page.getFlashType() == "error")
            <div class="alert alert-danger" role="alert">
                ${page.getFlash()}
            </div>
            @elseif(page.getFlashType() == "warning")
            <div class="alert alert-warning" role="alert">
                ${page.getFlash()}
            </div>
            @else
            <div class="alert alert-info" role="alert">
                ${page.getFlash()}
            </div>
            @endif
        @endif

        ${content}
    </main>

    <footer class="bg-light py-3 mt-5">
        <div class="d-flex flex-column flex-md-row justify-content-center align-items-center small">
            <span>© ${java.time.Year.now().getValue()} <strong>Екатерина Авдеенко</strong></span>
            <span>Анализатор страниц</span>
            <span>Учебный проект на <a href="https://ru.hexlet.io/" target="_blank">Hexlet</a></span>
            <span><a href="https://github.com/[ваш-username]/[ваш-репозиторий]" target="_blank" class="link-dark link-underline-opacity-0">GitHub</a></span>
            <span><a href="mailto:Katherini17@yandex.ru" class="link-success link-underline-opacity-0">Katherini17@yandex.ru</a></span>
        </div>
    </footer>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js" integrity="sha384-FKyoEForCGlyvwx9Hj09JcYn3nv7wiPVlz7YYwJrWVcXK/BmnVDxM+D2scQbITxI" crossorigin="anonymous"></script>
</body>
</html>

