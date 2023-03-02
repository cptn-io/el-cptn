import { useContext } from 'react';
import ThemeContext from '../context/ThemeContext';

const useThemes = () => useContext(ThemeContext);


export default useThemes;