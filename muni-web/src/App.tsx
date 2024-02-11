import { ThemeProvider, createTheme, responsiveFontSizes } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { AuthProvider } from './contexts/AuthContext';
import Home from './pages/home/Home';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Navbar from './components/navbar/Navbar';
import styles from './App.module.css'
import Account from './pages/account/Account';
import { darkScrollbar } from '@mui/material';
import { QueryClient, QueryClientProvider } from 'react-query';

const darkTheme = createTheme({
  palette: {
    mode: 'dark'
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: (theme) => ({
        '*': theme.palette.mode === 'dark' ? darkScrollbar() : null
      })
    }
  }
});

const theme = responsiveFontSizes(darkTheme);
const queryClient = new QueryClient()

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <QueryClientProvider client={queryClient}>
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
      </QueryClientProvider>
    </ThemeProvider>
  );
}

export default App;
