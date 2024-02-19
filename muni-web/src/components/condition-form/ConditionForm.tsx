import ClearIcon from '@mui/icons-material/Clear';
import DoneIcon from '@mui/icons-material/Done';
import { FormControl, IconButton, InputLabel, MenuItem, Select, TextField } from "@mui/material";
import { useState } from "react";
import { ConditionDto } from "../../api/dtos";

type Props = {
    condition?: ConditionDto;
    index?: number;
    addCondition: (arg0: ConditionDto) => void;
    removeCondition: (arg0: number) => void;
};

const ConditionForm = ({ condition, index, addCondition, removeCondition }: Props) => {
    const [column, setColumn] = useState(condition?.transactionColumn || '');
    const [operation, setOperation] = useState(condition?.operation || '');
    const [value, setValue] = useState(condition?.value || '');
    const [separator, setSeparator] = useState(condition?.separator || '');

    const save = (event: React.FormEvent) => {
        event.preventDefault();

        addCondition({
            id: condition?.id,
            transactionColumn: column,
            operation,
            value,
            separator: separator !== '' ? separator : undefined
        });
    }

    const remove = () => {
        if (index !== undefined) {
            removeCondition(index);
        }
    }

    const changed = () => {
        if (condition?.transactionColumn !== column || condition?.operation !== operation || condition?.value !== value) {
            return true;
        }

        return false;
    }

    return (
        <form style={{ display: 'flex' }} onSubmit={save}>
            {index !== undefined && index !== 0 &&
                <FormControl sx={{ minWidth: 110, marginRight: '8px' }}>
                    <InputLabel id="separator-label">Separator</InputLabel>
                    <Select autoWidth labelId="separator-label" label="Column" value={separator} onChange={(e) => setSeparator(e.target.value)} required>
                        <MenuItem value={'AND'}>{'And'}</MenuItem>
                        <MenuItem value={'OR'}>{'Or'}</MenuItem>
                    </Select>
                </FormControl>
            }

            <FormControl sx={{ minWidth: 140, marginRight: '8px' }}>
                <InputLabel id="column-label">Column</InputLabel>
                <Select autoWidth labelId="column-label" label="Column" value={column} onChange={(e) => setColumn(e.target.value)} required>
                    <MenuItem value={'AMOUNT'}>{'Amount'}</MenuItem>
                    <MenuItem value={'REMITTANCE_INFO'}>{'Information'}</MenuItem>
                </Select>
            </FormControl>

            <FormControl sx={{ minWidth: 140, marginRight: '8px' }}>
                <InputLabel id="operation-label">Operation</InputLabel>
                <Select autoWidth labelId="operation-label" label="Operation" value={operation} onChange={(e) => setOperation(e.target.value)} required>
                    <MenuItem value={'EQUALS'}>{'equals'}</MenuItem>
                    <MenuItem value={'CONTAINS'}>{'contains'}</MenuItem>
                </Select>
            </FormControl >

            <TextField sx={{ marginRight: '8px' }} fullWidth required label="Value" value={value} onChange={(e) => setValue(e.target.value)} />

            {changed() &&
                <IconButton size="large" type="submit">
                    <DoneIcon fontSize="inherit" />
                </IconButton>
            }

            {index !== undefined &&
                <IconButton size="large" onClick={remove}>
                    <ClearIcon fontSize="inherit" />
                </IconButton>
            }
        </form>
    );
};

export default ConditionForm;