import { createContext, useCallback, useEffect, useState } from "react";
const ThemeContext = createContext()

const themes = ['light', 'dark'];

export const ThemeContextProvider = (props) => {
    const [currentTheme, setCurrentTheme] = useState();

    const updateTheme = useCallback((theme) => {
        if (themes.indexOf(theme) > -1) {
            setCurrentTheme(theme);
        }
    }, []);

    const changeTheme = theme => {
        updateTheme(theme);
    }

    useEffect(() => {
        if (currentTheme) {
            document.documentElement.setAttribute("data-theme", currentTheme);
            localStorage.setItem('theme', currentTheme);
        }
    }, [currentTheme]);

    useEffect(() => {
        let theme = localStorage.getItem("theme");
        updateTheme(theme);
    }, [updateTheme]);

    return <ThemeContext.Provider value={{
        themes,
        currentTheme,
        changeTheme
    }}>{props.children}</ThemeContext.Provider>
};

export default ThemeContext;