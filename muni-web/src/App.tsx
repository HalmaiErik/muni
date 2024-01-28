import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { AuthProvider } from './contexts/AuthContext';
import Home from './pages/home/Home';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Navbar from './components/navbar/Navbar';
import styles from './App.module.css'
import Account from './pages/account/Account';

const darkTheme = createTheme({
  palette: {
    mode: 'dark'
  }
});

function App() {
  return (
    <ThemeProvider theme={darkTheme}>
      <CssBaseline />
      <Router>
        <AuthProvider>
          <Navbar />
          <div className={styles.pageContent}>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/:accountExternalId" element={<Account />} />
            </Routes>
          </div>
        </AuthProvider>
      </Router>
    </ThemeProvider>
  );
}

export default App;
