document.addEventListener('DOMContentLoaded', e => {
    const asideDialog = document.getElementById('aside-dialog');

    document.addEventListener('click', e => {
        const selector = Object.keys(clickHandlers).find(selector => e.target.closest(selector));
        clickHandlers[selector]?.(e);
    });

    const clickHandlers = {
        '.open-aside': e => {
            asideDialog.showModal();
        },
        '#aside-dialog': e => {
            if (e.target === asideDialog) {
                asideDialog.close();
            }
        }
    };

});
