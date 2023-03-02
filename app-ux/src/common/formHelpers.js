export const renderErrors = (error, field) => {
    return error?.fieldErrors?.[field]?.map(error => <label key={`${error}`} className="label">
        <span className="label-text-alt text-error">{error}</span>
    </label>)
}