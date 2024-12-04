use super::Evaluate;
use super::NodeBoolean;
use super::NodeNumeric;
use std::fmt::Display;
use std::fmt::Formatter;

pub enum NodeColour {
    If(Box<NodeBoolean>, Box<NodeColour>, Box<NodeColour>),
    RGB(Box<NodeNumeric>, Box<NodeNumeric>, Box<NodeNumeric>),
    GrammarBranch(String),
}

impl Display for NodeColour {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        match self {
            NodeColour::If(cond, then, other) => {
                write!(f, "If ({}) then ({}) else ({})", cond, then, other)
            }
            NodeColour::RGB(r, g, b) => write!(f, "RGB({}, {}, {})", r, g, b),
            NodeColour::GrammarBranch(s) => {
                write!(f, "{}", s)
            }
        }
    }
}

impl Evaluate for NodeColour {
    type Output = (u8, u8, u8);
    fn eval(&self, x: &f32, y: &f32) -> Result<(u8, u8, u8), &'static str> {
        match self {
            NodeColour::If(cond, then, other) => {
                if cond.eval(x, y)? {
                    then.eval(x, y)
                } else {
                    other.eval(x, y)
                }
            }
            NodeColour::RGB(r, g, b) => {
                let r = to_u8(r.eval(x, y)?)?;
                let g = to_u8(g.eval(x, y)?)?;
                let b = to_u8(b.eval(x, y)?)?;
                Ok((r, g, b))
            }
            NodeColour::GrammarBranch(..) => Err("Grammar branch must not be evaluated"),
        }
    }
}

fn to_u8(i: f32) -> Result<u8, &'static str> {
    if i < -1.0 || i > 1.0 {
        return Err("x must be between -1 and 1");
    }
    Ok(((i + 1.0) * 128.0) as u8)
}
